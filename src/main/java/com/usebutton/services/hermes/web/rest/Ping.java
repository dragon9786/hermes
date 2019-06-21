package com.usebutton.services.hermes.web.rest;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Handles ping for "is it up?" queries
 *
 * @author Govind
 */
@RestController
public class Ping {
    @RequestMapping(value="/ping")
    public ResponseEntity getPing()
    {
        return new ResponseEntity<String>("pong", HttpStatus.OK);
    }
}
