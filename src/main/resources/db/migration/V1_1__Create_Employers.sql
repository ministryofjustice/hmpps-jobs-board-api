create table job_employers (employer_id bigint not null, created_by varchar(255), created_date_time timestamp(6), employer_bio varchar(255), employer_name varchar(255), grade varchar(255) , image_path varchar(255) , modified_by varchar(255), modified_date_time timestamp(6), partner_name varchar(255) , post_code varchar(8), sector_name varchar(255) , primary key (employer_id));
create sequence IF NOT EXISTS hibernate_seq start with 1 increment by 50;
create sequence IF NOT EXISTS job_employers_seq start with 1 increment by 50;
