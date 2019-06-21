FROM button/java:8-oracle
COPY ./build/libs/hermes.jar ./src/main/jmx_exporter/jmx_exporter_java_agent.jar /opt/hermes/lib/
COPY ./src/main/jmx_exporter/jmx_exporter.yml /opt/hermes/config/exporter.yml
CMD ["java", "-Dspring.profiles.active=docker", "-javaagent:/opt/hermes/lib/jmx_exporter_java_agent.jar=7199:/opt/hermes/config/exporter.yml", "-jar", "/opt/hermes/lib/hermes.jar"]
