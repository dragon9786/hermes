package com.usebutton.services.hermes.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;
import java.util.Date;


/**
 * Created by govind on 6/19/19.
 */
@Getter @Setter
public class Location {
    private Double lat;
    private Double lng;
    private String address1;
    private String address2;
    private String placeName;
    private String cityName;
    private String offerDescription;
    private String offerEndDate;

    @JsonCreator
    public Location(@JsonProperty("lat") Double lat,
                    @JsonProperty("lng") Double lng,
                    @JsonProperty("address_1") String address1,
                    @JsonProperty("address_2") String address2,
                    @JsonProperty("placeName") String placeName,
                    @JsonProperty("cityName") String cityName,
                    @JsonProperty("offerDescription") String offerDescription,
                    @JsonProperty("offerEndDate") String offerEndDate) {
        this.lat = lat;
        this.lng = lng;

        this.address1 = address1;
        this.address2 = address2;
        this.placeName = placeName;
        this.cityName = cityName;
        this.offerDescription = offerDescription;
        this.offerEndDate = offerEndDate;
    }
}
