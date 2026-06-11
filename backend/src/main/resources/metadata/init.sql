CREATE DATABASE `api_hub` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
create user 'api_hub'@'%' identified by 'api_hub@2021';
grant all privileges on *.* to 'api_hub'@'%' with grant option;
flush privileges;

-- data source
drop table if exists api_hub.data_source_hbase;
create table api_hub.data_source_hbase
(
    `id`              serial,
    `name`            varchar(128) not null,
    `comments`        varchar(256)          default null,
    `hbase_site_path` varchar(256) not null,
    `core_site_path`  varchar(256) not null,
    `status`          int                   default 1,
    `create_time`     timestamp    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `create_by`       varchar(128),
    `update_time`     timestamp    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `update_by`       varchar(128)
);

drop table if exists api_hub.data_source_mysql;
create table api_hub.data_source_mysql
(
    `id`          serial,
    `name`        varchar(128) not null,
    `comments`    varchar(256)          default null,
    `url`         varchar(256) not null,
    `username`    varchar(128) not null,
    `password`    varchar(128) not null,
    `status`      int                   default 1,
    `create_time` timestamp    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `create_by`   varchar(128),
    `update_time` timestamp    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `update_by`   varchar(128)
);

drop table if exists api_hub.data_source_solr;
create table api_hub.data_source_solr
(
    `id`          serial,
    `name`        varchar(128) not null,
    `comments`    varchar(256)          default null,
    `zk_hosts`    varchar(256) not null,
    `zk_chroots`  varchar(128) not null,
    `status`      int                   default 1,
    `create_time` timestamp    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `create_by`   varchar(128),
    `update_time` timestamp    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `update_by`   varchar(128)
);

drop table if exists api_hub.data_source_sql;
create table api_hub.data_source_sql
(
    `id`          serial,
    `name`        varchar(128) not null,
    `comments`    varchar(256)          default null,
    `dialect`     varchar(128) not null,
    `url`         varchar(128) not null,
    `username`    varchar(128) not null,
    `password`    varchar(128) not null,
    `status`      int                   default 1,
    `create_time` timestamp    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `create_by`   varchar(128),
    `update_time` timestamp    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `update_by`   varchar(128)
);

-- data api
drop table if exists api_hub.data_api_hbase;
create table api_hub.data_api_hbase
(
    `id`             serial,
    `data_source_id` bigint        not null,
    `name`           varchar(128)  not null,
    `comments`       varchar(256)           default null,
    `type`           tinyint       not null,
    `table_name`     varchar(256)  not null,
    `columns`        varchar(1024) not null,
    `status`         int                    default 1,
    `create_time`    timestamp     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `create_by`      varchar(128),
    `update_time`    timestamp     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `update_by`      varchar(128)
);

drop table if exists api_hub.data_api_mysql;
create table api_hub.data_api_mysql
(
    `id`              serial,
    `data_source_id`  bigint        not null,
    `name`            varchar(128)  not null,
    `comments`        varchar(256)           default null,
    `database_name`   varchar(256)  not null,
    `table_name`      varchar(256)  not null,
    `columns`         varchar(1024) not null,
    `conditions`      varchar(256),
    `condition_types` varchar(256),
    `orders`          varchar(256),
    `status`          int                    default 1,
    `create_time`     timestamp     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `create_by`       varchar(128),
    `update_time`     timestamp     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `update_by`       varchar(128)
);

drop table if exists api_hub.data_api_solr;
create table api_hub.data_api_solr
(
    `id`             serial,
    `data_source_id` bigint        not null,
    `name`           varchar(128)  not null,
    `comments`       varchar(256)           default null,
    `collection`     varchar(256)  not null,
    `fields`         varchar(1024) not null,
    `conditions`     varchar(256),
    `orders`         varchar(256),
    `status`         int                    default 1,
    `create_time`    timestamp     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `create_by`      varchar(128),
    `update_time`    timestamp     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `update_by`      varchar(128)
);

drop table if exists api_hub.data_api_sql;
create table api_hub.data_api_sql
(
    `id`             serial,
    `data_source_id` bigint       not null,
    `name`           varchar(128) not null,
    `comments`       varchar(256)          default null,
    `sql`            text         not null,
    `page_tag`       int          not null default 0,
    `page_size`      int          not null default 1,
    `status`         int                   default 1,
    `create_time`    timestamp    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `create_by`      varchar(128),
    `update_time`    timestamp    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `update_by`      varchar(128)
);

drop table if exists api_hub.data_api_sql_param;
create table api_hub.data_api_sql_param
(
    `id`          serial,
    `api_id`      bigint       not null,
    `name`        varchar(128) not null,
    `type`        varchar(128) not null,
    `description` varchar(256)          default null,
    `status`      int                   default 1,
    `create_time` timestamp    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `create_by`   varchar(128),
    `update_time` timestamp    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `update_by`   varchar(128)
);
