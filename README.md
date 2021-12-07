# API HUB

The original intention of this project is to unify the management of the internal data platform api of the enterprise, 
including API registration, authorization, monitoring, etc. This is quite useful for enterprise IT management and data management.
I start this project for my self-improvement, and to provide reference for those in need.

### Features

1. Currently, API HUB support Solr, HBase and Mysql API management. 
2. Data source registration, enable/disable, list.
3. API registration, enable/disable list.

### Environment

1. JDK 1.8+
2. A database service, like Mysql, for metadata persistence. Please refer to the resources/metadata/init.sql for more details.

### Building

```shell
mvn clean package
```

### TODO

1. Add support for Elasticsearch, Clickhouse, Redis, etc.
2. High availability and service gateway features.
3. Users, user groups and permissions.
4. Logs and alarms.