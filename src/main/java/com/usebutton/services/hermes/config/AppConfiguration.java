package com.usebutton.services.hermes.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Main configuration class
 *
 * @author Govind
 */
@Configuration
public class AppConfiguration {

    @Autowired
    private Environment environment;

    public String getServiceName() {
        return "herme";
    }

    @Value("${server.port}")
    private int serverPort;
    public int getServerPort() {
        return serverPort;
    }

    private String serviceId;
    public String getServiceId() {
        if (serviceId == null) {
            serviceId = Integer.toString(serverPort);
        }
        return serviceId;
    }

    @Value("${server.hostIP}")
    private String serviceIPAddress;
    public String getServiceIPAddress() {
        if (serviceIPAddress == null || serviceIPAddress.isEmpty()) {
            try {
                serviceIPAddress = InetAddress.getLocalHost().getHostAddress();
            } catch (UnknownHostException e) {
                serviceIPAddress = "127.0.0.1";
            }
        }
        return serviceIPAddress;
    }
}
