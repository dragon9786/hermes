package com.usebutton.services.hermes.services;

import com.usebutton.services.hermes.config.Constants;
import com.usebutton.services.hermes.config.HbaseConfiguration;
import com.vividsolutions.jts.geom.Point;
import lombok.Getter;
import org.geotools.data.DataStore;
import org.geotools.data.DataStoreFinder;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.feature.SchemaException;
import org.geotools.feature.simple.SimpleFeatureTypeBuilder;
import org.locationtech.geomesa.utils.interop.SimpleFeatureTypes;
import org.opengis.feature.simple.SimpleFeatureType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


/**
 * Created by govind on 6/18/19.
 */
@Service
public class HbaseService {

    final private Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    @Getter
    private SimpleFeatureType simpleDwellPointFeatureType;

    @Getter
    private DataStore dataStore;

    @Getter
    private HbaseConfiguration hbaseConfiguration;


    public HbaseService(HbaseConfiguration hbaseConfiguration) throws Exception, IOException, SchemaException {
        this.hbaseConfiguration = hbaseConfiguration;
        try {
            this.dataStore = this.hbaseConfiguration.getHbaseDataStore();
            this.simpleDwellPointFeatureType = createSimpleFeatureType(Constants.BUTTON_LOCATION_POINT_FEATURE_NAME);
            this.dataStore.createSchema(simpleDwellPointFeatureType);
            LOGGER.info("Succesfully initialized Hbase datastore and created features");
        } catch (IOException ex) {
            LOGGER.error("Error Initializing datastore for Hbase ", ex);
            throw ex;
        } catch (SchemaException ex) {
            LOGGER.error("Error Initializing feature types in Hbase", ex);
            throw ex;
        } catch (Exception ex) {
            LOGGER.error("Something bad happened while initializiang Hbase ", ex);
            throw ex;
        }
    }

    public SimpleFeatureSource getFeatureSource() throws IOException{
        return this.dataStore.getFeatureSource(Constants.BUTTON_LOCATION_POINT_FEATURE_NAME);
    }


    /**
     *
     * @param simpleFeatureTypeName Name of the simple feature to create
     * @return SimpleFeatureType
     * @throws SchemaException
     */
    public SimpleFeatureType createSimpleFeatureType(String simpleFeatureTypeName) throws SchemaException {

        LOGGER.debug("Creating feature-type (schema): {}", Constants.BUTTON_LOCATION_POINT_FEATURE_NAME);
        SimpleFeatureTypeBuilder builder = new SimpleFeatureTypeBuilder();
        builder.setName(simpleFeatureTypeName);

        // add attributes in order
        builder.add("lat", Double.class);
        builder.add("lng", Double.class);
        builder.add("locationPoint", Point.class);
        builder.add("cityName", String.class);
        builder.add("address1", String.class);
        builder.add("address2", String.class);
        builder.add("placeName", String.class);
        builder.add("offerDescription", String.class);
        builder.add("offerEndTime", Date.class);
        // build the type
        SimpleFeatureType location = builder.buildFeatureType();

        location.getUserData().put(SimpleFeatureTypes.DEFAULT_DATE_KEY, "offerEndTime");

        // Note we use full index to minimize joins within the features
        location.getDescriptor("placeName").getUserData().put("index", "full");
        location.getDescriptor("placeName").getUserData().put("cardinality", "high");
        location.getDescriptor("cityName").getUserData().put("index", "full");
        location.getDescriptor("cityName").getUserData().put("cardinality", "full");


        return location;
    }
}
