CREATE DATABASE `api_hub` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- data source
create table api_hub.data_source_hbase(
`id` serial,
`name` varchar(128) not null,
`comments` varchar(256) default null,
`hbase_site_path` varchar(256) not null,
`core_site_path` varchar(256) not null,
`status` int default 1,
`create_time` timestamp,
`create_by` varchar(128),
`update_time` timestamp,
`update_by` varchar(128)
);

create table api_hub.data_source_mysql(
`id` serial,
`name` varchar(128) not null,
`comments` varchar(256) default null,
`url` varchar(256) not null,
`username` varchar(128) not null,
`password` varchar(128) not null,
`status` int default 1,
`create_time` timestamp,
`create_by` varchar(128),
`update_time` timestamp,
`update_by` varchar(128)
);

create table api_hub.data_source_solr(
`id` serial,
`name` varchar(128) not null,
`comments` varchar(256) default null,
`zk_hosts` varchar(256) not null,
`zk_chroots` varchar(128) not null,
`status` int default 1,
`create_time` timestamp,
`create_by` varchar(128),
`update_time` timestamp,
`update_by` varchar(128)
);

-- data api
create table api_hub.data_api_hbase(
`id` serial,
`data_source_id` bigint not null,
`name` varchar(128) not null,
`comments` varchar(256) default null,
`type` tinyint not null,
`table_name` varchar(256) not null,
`columns` varchar(1024) not null,
`status` int default 1,
`create_time` timestamp,
`create_by` varchar(128),
`update_time` timestamp,
`update_by` varchar(128)
);

create table api_hub.data_api_mysql(
`id` serial,
`data_source_id` bigint not null,
`name` varchar(128) not null,
`comments` varchar(256) default null,
`database_name` varchar(256) not null,
`table_name` varchar(256) not null,
`columns` varchar(1024) not null,
`conditions` varchar(256),
`condition_types` varchar(256),
`orders` varchar(256),
`status` int default 1,
`create_time` timestamp,
`create_by` varchar(128),
`update_time` timestamp,
`update_by` varchar(128)
);

create table api_hub.data_api_solr(
`id` serial,
`data_source_id` bigint not null,
`name` varchar(128) not null,
`comments` varchar(256) default null,
`collection` varchar(256) not null,
`fields` varchar(1024) not null,
`conditions` varchar(256),
`orders` varchar(256),
`status` int default 1,
`create_time` timestamp,
`create_by` varchar(128),
`update_time` timestamp,
`update_by` varchar(128)
);
