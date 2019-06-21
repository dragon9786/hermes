package com.usebutton.services.hermes.response;

import com.usebutton.services.hermes.config.Constants;
import com.usebutton.services.hermes.services.GeoMesaService;
import lombok.Getter;
import lombok.Setter;
import org.geotools.feature.FeatureIterator;
import org.opengis.feature.Feature;

import java.io.Serializable;
import java.lang.reflect.MalformedParametersException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.IntSummaryStatistics;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Created by govind on 6/19/19
 */
@Getter @Setter
public class LocationResponse implements Serializable {
    private List<ButtonLocationResponse> buttonLocationResponseList;

    public LocationResponse(GeoMesaService geoMesaService,
                            String cityName,
                            int maxFeatures,
                            String sortByField) throws Exception {

        this.buttonLocationResponseList = new LinkedList<ButtonLocationResponse>();

        String attributes = this.getQueryForAttributes(cityName);
        FeatureIterator featureIterator = geoMesaService.queryDwellLocationFeature(attributes,
                maxFeatures,
                sortByField);
        try {
            this.buildButtonLocationResponse(featureIterator);
        } finally {
            featureIterator.close();
        }
    }

    // Default base attribute query
    private String getQueryForAttributes(String cityName) throws Exception {
        // Note that the caller should provide sane values to all the query parameters
        // to prevent a full table scan in the off chance

//        // Convert DateTime strings to a UTC ZonedDateTimeString
//        String endTimeMin = ZonedDateTime.parse(endMin)
//                .withZoneSameInstant(ZoneId.of("UTC"))
//                .toInstant()
//                .toString();
//
//        String endTimeMax = ZonedDateTime.parse(endMax)
//                .withZoneSameInstant(ZoneId.of("UTC"))
//                .toInstant()
//                .toString();
//
//        String attributes = String.format("(endTime DURING %s/%s) AND (circleId = '%s')",
//                endTimeMin, endTimeMax, circleId);
//        return (placeId == null) ? attributes : attributes + String.format("AND (placeId = '%s')", placeId);
        String attributes = String.format("(cityName = '%s')", cityName);
        return attributes;
    }

    private void buildButtonLocationResponse(FeatureIterator featureIterator) throws Exception {
        while (featureIterator.hasNext()) {
            Feature feature = featureIterator.next();
            ButtonLocationResponse buttonLocation = ButtonLocationResponse.buildResponse(feature);
            this.buttonLocationResponseList.add(buttonLocation);
        }
    }
}