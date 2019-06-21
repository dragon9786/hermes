package com.usebutton.services.hermes.services;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Base service for common functionality
 */
public class ButtonLocationService {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    protected Logger getLogger() {
        return logger;
    }

}
