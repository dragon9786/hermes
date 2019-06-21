package com.usebutton.services.hermes.config;

import org.apache.catalina.connector.Connector;
import org.apache.catalina.Context;
import org.apache.catalina.webresources.StandardRoot;
import org.apache.coyote.ProtocolHandler;
import org.apache.coyote.http11.AbstractHttp11Protocol;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.embedded.EmbeddedServletContainerFactory;
import org.springframework.boot.context.embedded.tomcat.TomcatConnectorCustomizer;
import org.springframework.boot.context.embedded.tomcat.TomcatContextCustomizer;
import org.springframework.boot.context.embedded.tomcat.TomcatEmbeddedServletContainerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration details for the database to which we'll be connecting
 */
@Configuration
@ConfigurationProperties("web")
public class WebConfiguration {
    final private Logger logger = LoggerFactory.getLogger(this.getClass());

    private int maxKeepAliveRequests = 100000;
    public void setMaxKeepAliveRequests(int maxKeepAliveRequests) {
        this.maxKeepAliveRequests = maxKeepAliveRequests;
    }

    private boolean letCatalinaCache = false;
    public void setLetCatalinaCache(boolean letCatalinaCache) {
        this.letCatalinaCache = letCatalinaCache;
    }

    @Bean
    public EmbeddedServletContainerFactory servletContainer() {
        TomcatEmbeddedServletContainerFactory tomcat = new TomcatEmbeddedServletContainerFactory();
        tomcat.addContextCustomizers(
            new TomcatContextCustomizer() {
                public void customize(Context context) {
                    StandardRoot standardRoot = new StandardRoot(context);
                    standardRoot.setCachingAllowed(letCatalinaCache);
                    // This is the key according to http://stackoverflow.com/questions/39146476
                    context.setResources(standardRoot);
                    logger.info("IsCachingAllowed {}", letCatalinaCache);
                }
            });
        tomcat.addConnectorCustomizers(
            new TomcatConnectorCustomizer() {
                public void customize(Connector connector) {
                    ProtocolHandler handler = connector.getProtocolHandler();
                    if (handler instanceof AbstractHttp11Protocol) {
                        ((AbstractHttp11Protocol<?>) handler).setMaxKeepAliveRequests(maxKeepAliveRequests);
                        logger.info("ProtocolHandler {} MaxKeepAliveRequests {}",
                                    connector.getProtocolHandlerClassName(), maxKeepAliveRequests);
                    }
                }
          });
        return tomcat;
    }
}
