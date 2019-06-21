package com.usebutton.services.hermes.config;

import lombok.Getter;
import lombok.Setter;
import org.geotools.data.DataStore;
import org.geotools.data.DataStoreFinder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by govind on 6/20/19.
 */
@Configuration
@ConfigurationProperties("hbase")
public class HbaseConfiguration {
    private static final Logger LOGGER = LoggerFactory.getLogger(HbaseConfiguration.class);

    @Getter @Setter
    private String tableName;


    public Map<String, Object> buildHbaseParams() {
        Map<String, Object> hbaseParams = new HashMap<>();
        hbaseParams.put("bigtable.table.name",this.getTableName());
        return hbaseParams;
    }

    public DataStore getHbaseDataStore() throws Exception {
        return DataStoreFinder.getDataStore(this.buildHbaseParams());
    }

}
