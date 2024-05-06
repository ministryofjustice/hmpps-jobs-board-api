drop table IF EXISTS cm_profile CASCADE;
drop sequence IF EXISTS hibernate_sequence;
create table IF NOT EXISTS CM_PROFILE (offender_id varchar(255) not null, created_by varchar(255), created_date_time timestamp, modified_by varchar(255), modified_date_time timestamp, schema_version varchar(255), primary key (offender_id));
