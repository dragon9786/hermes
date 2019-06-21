
# Hermes
### Requirements
Heremes uses GeoMesa Library for Spatio Temporal indexing on top of Hbase
* Install hbase from [here](http://apache.claz.org/hbase/1.3.5/)
* Set `HBASE_HOME` environment variable to `your_download_path/bin`
* Run HBase in [stand-alone](https://hbase.apache.org/book.html#quickstart) mode.
* Install zookeeper and start it by running `zkServer start`
* Download `GEOMESA` and follow the instructions to copy the distributed runtime jar from [here](https://www.geomesa.org/documentation/user/hbase/install.html#building-from-source)
* Set `GEOMESA_HOME` environment variable to `your_download_path_to_geomesa`

Once you have everything installed set the following property in `$HBASE_HOME/conf/hbase-site.xml`

```
 <property>
    <name>hbase.coprocessor.user.region.classes</name>
    <value>org.locationtech.geomesa.hbase.coprocessor.GeoMesaCoprocessor</value>
  </property>
```

Restart Hbase by stpopping ans starting it, using `$HBASE_HOME/bin/stop-hbase.sh` and `$HBASE_HOME/bin/stop-hbase.sh`

Run `make server` on the root of this repo

# References
### Extended Contextual Query Language (was Common Query Language)
http://docs.geoserver.org/stable/en/user/filter/ecql_reference.html

### Geomesa Hbase Quickstart
http://www.geomesa.org/documentation/tutorials/geomesa-quickstart-hbase.html
å