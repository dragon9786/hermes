package com.usebutton.services.hermes.response;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import org.opengis.feature.Feature;

import java.util.Date;
import java.util.UUID;

/**
 * Created by govind on 6/20/19.
 */
@Getter
@Setter
public class ButtonLocationResponse {
    private Double lat;
    private Double lng;
    private String cityName;
    private String address1;
    private String address2;
    private String placeName;
    private String offerDescription;
    private Date offerEndTime;

    public static ButtonLocationResponse buildResponse(Feature feature) {
        ButtonLocationResponse resp = new ButtonLocationResponse();
        resp.lat = (Double) feature.getProperty("lat").getValue();
        resp.lng = (Double) feature.getProperty("lng").getValue();
        resp.cityName = (String) feature.getProperty("cityName").getValue();
        resp.address1 = (String) feature.getProperty("address1").getValue();
        resp.address2 = (String) feature.getProperty("address2").getValue();
        resp.offerEndTime = (Date) feature.getProperty("offerEndTime").getValue();
        resp.placeName = (String) feature.getProperty("placeName").getValue();
        resp.offerDescription = (String) feature.getProperty("offerDescription").getValue();
        return resp;
    }
}
