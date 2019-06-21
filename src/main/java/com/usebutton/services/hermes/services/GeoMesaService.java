package com.usebutton.services.hermes.services;

import com.usebutton.services.hermes.config.Constants;
import com.usebutton.services.hermes.domain.Location;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;
import lombok.Getter;
import org.geotools.data.FeatureSource;
import org.geotools.data.Query;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.data.simple.SimpleFeatureStore;
import org.geotools.factory.CommonFactoryFinder;
import org.geotools.feature.DefaultFeatureCollection;
import org.geotools.feature.FeatureIterator;
import org.geotools.feature.SchemaException;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.geotools.feature.simple.SimpleFeatureTypeBuilder;
import org.geotools.filter.text.cql2.CQL;
import org.geotools.filter.text.cql2.CQLException;
import org.geotools.geometry.jts.JTSFactoryFinder;
import org.locationtech.geomesa.utils.interop.SimpleFeatureTypes;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.filter.Filter;
import org.opengis.filter.FilterFactory2;
import org.opengis.filter.sort.SortBy;
import org.opengis.filter.sort.SortOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.Duration;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;


/**
 *  Service for writing data into GeoMesa
 * Created by govind on 6/20/19.
 */
@Service
public class GeoMesaService {

    final private Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    private HbaseService hbaseService;

    private static final Map<String, String> sortFieldMapping = new HashMap<String, String>() {{
        put("end", "endTime");
    }};


    public GeoMesaService(HbaseService hbaseService) throws Exception {
        this.hbaseService = hbaseService;
    }

    public void saveLocationData(Double lat, Double lng, String cityName, String placeName, String address1, String address2, String offerDescription, String offerEndDate) throws Exception {
        Location loc = new Location(lat, lng, address1, address2, placeName, cityName, offerDescription, offerEndDate);
        saveLocationData(loc);

    }
    public void saveLocationData(Location loc) throws Exception {
        //Create Feature from Location Point and store into Hbase
        SimpleFeatureCollection featureCollection = createNewDwellLocationFeature(this.hbaseService.getSimpleDwellPointFeatureType(), loc);
        try {
            SimpleFeatureSource featureSource = this.hbaseService.getFeatureSource();
            if (featureSource instanceof SimpleFeatureStore) {
                SimpleFeatureStore featureStore = (SimpleFeatureStore) featureSource;
                featureStore.addFeatures(featureCollection);
                LOGGER.info("Succesfully saved Data in Hbase");
            }
            else {
                LOGGER.warn("SAVE-LOCATION-DATA FeatureSource is not a FeatureStore");
            }
        } catch (Exception e) {
            LOGGER.error("Error adding Dwell Location feature in hbase ", e);
            throw e;
        }
    }


    public SimpleFeatureCollection createNewDwellLocationFeature(SimpleFeatureType simpleDwellPointFeatureType, Location loc) {
        DefaultFeatureCollection featureCollection = new DefaultFeatureCollection();
        GeometryFactory geometryFactory;
        SimpleFeatureBuilder featureBuilder;

        ZonedDateTime offerEndDate;
        Double lat;
        Double lng;
        Point buttonLocationPoint;
        try {
            offerEndDate = ZonedDateTime.parse(loc.getOfferEndDate())
                    .withZoneSameInstant(ZoneId.of("UTC"));
            geometryFactory = JTSFactoryFinder.getGeometryFactory(null);
            featureBuilder = new SimpleFeatureBuilder(simpleDwellPointFeatureType);
            lat = loc.getLat();
            lng = loc.getLng();
            buttonLocationPoint = geometryFactory.createPoint(new Coordinate(lng, lat));
        } catch (Exception exc) {
            LOGGER.error("Failed location feature initialization", exc);
            return featureCollection;
        }
        featureBuilder.add(lat);
        featureBuilder.add(lng);
        featureBuilder.add(buttonLocationPoint);
        featureBuilder.add(loc.getCityName());
        featureBuilder.add(loc.getAddress1());
        featureBuilder.add(loc.getAddress2());
        featureBuilder.add(loc.getPlaceName());
        featureBuilder.add(loc.getOfferDescription());
        featureBuilder.add(Date.from(offerEndDate.toInstant()));

        SimpleFeature simpleFeature = featureBuilder.buildFeature(null);
        featureCollection.add(simpleFeature);
        return featureCollection;
    }



    /**
     * @param attributesQuery
     * @param maxFeatures
     * @param sortByField
     * @return FeatureIterator Iterator for the constructed cql queries
     * @throws CQLException
     * @throws IOException
     */
    public FeatureIterator queryDwellLocationFeature(String attributesQuery,
                                                     int maxFeatures,
                                                     String sortByField) throws CQLException, IOException {
        Filter cqlFilter = createFilter(attributesQuery);
        Query query = new Query(Constants.BUTTON_LOCATION_POINT_FEATURE_NAME, cqlFilter);
        query.setMaxFeatures(maxFeatures);

        if (!sortByField.equals("")) {
            sortByField = this.sortFieldMapping.getOrDefault(sortByField, sortByField);
            FilterFactory2 ff = CommonFactoryFinder.getFilterFactory2();
            SortBy[] sort = new SortBy[]{ff.sort(sortByField, SortOrder.DESCENDING)};
            query.setSortBy(sort);
        }

        FeatureSource featureSource = this.hbaseService.getFeatureSource();
        FeatureIterator featureItr = featureSource.getFeatures(query).features();
        return featureItr;
    }


    /**
     *
     * @param attributesQuery
     * @return Filter The CQL filter to be used by the query
     * @throws CQLException
     * @throws IOException
     */
    public Filter createFilter(String attributesQuery) throws CQLException, IOException {
        // the GeoTools Filter constant "INCLUDE" is a default that means to accept everything
        return CQL.toFilter((attributesQuery == null) ? "INCLUDE" : attributesQuery);
    }
}
