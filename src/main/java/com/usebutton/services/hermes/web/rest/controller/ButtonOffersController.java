package com.usebutton.services.hermes.web.rest.controller;

import com.usebutton.services.hermes.response.LocationResponse;

import com.usebutton.services.hermes.services.GeoMesaService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;

import org.springframework.web.bind.annotation.RestController;


import java.io.IOException;
import java.util.UUID;
import java.util.Date;

/**
 * Created by govind on 6/20/19.
 */

@RestController
@RequestMapping("/offers")
@CacheConfig(cacheNames="button-offers")
public class ButtonOffersController {

    private GeoMesaService geoMesaService;

    @Autowired
    public  ButtonOffersController(GeoMesaService geoMesaService) {
        this.geoMesaService = geoMesaService;
    }

    // Gets all the offers for near a given lat/lng sorted desc by the field specified

    @RequestMapping(method=RequestMethod.GET)
    public @ResponseBody LocationResponse get(@RequestParam(value="city-name", required=true) String cityName,
                                              @RequestParam(value="max-results", required=true) int maxFeatures,
                                              @RequestParam(value="sort-by", required=true) String sortByField) throws Exception {
        return new LocationResponse(this.geoMesaService, cityName, maxFeatures, sortByField);
    }

    @PostMapping
    public @ResponseBody ResponseEntity<?> postLocation(@RequestParam(value="lat", required=true) Double lat,
                                                        @RequestParam(value="lng", required=true) Double lng,
                                                        @RequestParam(value="cityName", required=true) String cityName,
                                                        @RequestParam(value="placeName", required=true) String placeName,
                                                        @RequestParam(value="address1", required=true) String address1,
                                                        @RequestParam(value="address2", required=true) String address2,
                                                        @RequestParam(value="offerDescription", required=true) String offerDescription,
                                                        @RequestParam(value="offerEndDate", required=true) String offerEndDate) throws Exception {
        this.geoMesaService.saveLocationData(lat, lng, cityName, placeName, address1, address2, offerDescription, offerEndDate);
        return new ResponseEntity(HttpStatus.OK);
    }

}
