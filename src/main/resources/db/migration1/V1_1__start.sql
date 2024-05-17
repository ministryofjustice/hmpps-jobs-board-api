drop table
    IF EXISTS prison_leavers_job;
drop table
    IF EXISTS job_charity;

drop table
    IF EXISTS job_employers;

drop table
    IF EXISTS base_location;
drop table
    IF EXISTS employer_partner;
drop table
    IF EXISTS employer_partner_grades;
drop table
    IF EXISTS employer_work_sector;
drop table
    IF EXISTS hours_type;

drop table
    IF EXISTS job_contract_type;
drop table
    IF EXISTS job_image;
drop table
    IF EXISTS job_source_list;
drop table
    IF EXISTS job_source;

drop table
    IF EXISTS job_type;
drop table
    IF EXISTS offences_type_list;
drop table
    IF EXISTS offences_type;


drop table
    IF EXISTS salary_period;
drop table
    IF EXISTS work_pattern;



CREATE TABLE
    IF NOT EXISTS employer_work_sector
(
    sector_id      BIGINT NOT NULL,
    mn_id          BIGINT NOT NULL,
    mn_identifier  VARCHAR(255) NOT NULL,
    mn_sector_name VARCHAR(255) NOT NULL,
    PRIMARY KEY (sector_id)
    );
CREATE TABLE
    IF NOT EXISTS employer_partner_grades
(
    partner_grade_id BIGINT NOT NULL,
    mn_id            BIGINT NOT NULL,
    mn_identifier    VARCHAR(255) NOT NULL,
    mn_grade         VARCHAR(255) NOT NULL,
    PRIMARY KEY (partner_grade_id)
    );
CREATE TABLE
    IF NOT EXISTS employer_partner
(
    partner_id       BIGINT NOT NULL,
    partner_grade_id BIGINT NOT NULL,
    mn_id            BIGINT NOT NULL,
    mn_identifier    VARCHAR(255) NOT NULL,
    mn_partner_name  VARCHAR(255) NOT NULL,
    PRIMARY KEY (partner_id),
    CONSTRAINT fk_employer_partner_grades FOREIGN KEY(partner_grade_id) REFERENCES
    employer_partner_grades(partner_grade_id)
    );
CREATE TABLE
    IF NOT EXISTS job_image
(
    image_id   BIGINT NOT NULL,
    mn_id      BIGINT NOT NULL,
    image_path VARCHAR(255) NOT NULL,
    PRIMARY KEY (image_id)
    );
CREATE TABLE
    IF NOT EXISTS job_employers
(
    employer_id        BIGINT NOT NULL,
    employer_name      VARCHAR(255),
    employer_bio       VARCHAR(255),
    created_by         VARCHAR(255),
    created_date_time  TIMESTAMP,
    modified_by        VARCHAR(255),
    modified_date_time TIMESTAMP,
    sector_id          BIGINT NOT NULL,
    partner_id         BIGINT NOT NULL,
    image_id           BIGINT NOT NULL,
    post_code      VARCHAR(255),
    PRIMARY KEY (employer_id),
    CONSTRAINT fk_employer_sector FOREIGN KEY(sector_id) REFERENCES employer_work_sector
(sector_id),
    CONSTRAINT fk_employer_partner FOREIGN KEY(partner_id) REFERENCES employer_partner
(partner_id),
    CONSTRAINT fk_employer_image FOREIGN KEY(image_id) REFERENCES job_image(image_id)
    );
CREATE TABLE
    IF NOT EXISTS job_charity
(
    charity_id         BIGINT NOT NULL,
    charity_name_name  VARCHAR(255),
    charity_bio        VARCHAR(255),
    created_by         VARCHAR(255),
    created_date_time  TIMESTAMP,
    modified_by        VARCHAR(255),
    modified_date_time TIMESTAMP,
    image_id           BIGINT NOT NULL,
    PRIMARY KEY (charity_id),
    CONSTRAINT fk_employer_image FOREIGN KEY(image_id) REFERENCES job_image(image_id)
    );
CREATE TABLE
    IF NOT EXISTS base_location
(
    base_location_id BIGINT NOT NULL,
    mn_id            BIGINT NOT NULL,
    mn_identifier    VARCHAR(255) NOT NULL,
    mn_sector_name   VARCHAR(255) NOT NULL,
    PRIMARY KEY (base_location_id)
    );
CREATE TABLE
    IF NOT EXISTS job_contract_type
(
    job_contract_type_id      BIGINT NOT NULL,
    mn_id                BIGINT NOT NULL,
    mn_identifier        VARCHAR(255) NOT NULL,
    mn_job_contract_name VARCHAR(255) NOT NULL,
    PRIMARY KEY (job_contract_type_id)
    );
CREATE TABLE
    IF NOT EXISTS offences_type
(
    offences_id      BIGINT NOT NULL,
    mn_id            BIGINT NOT NULL,
    mn_identifier    VARCHAR(255) NOT NULL,
    mn_offences_name VARCHAR(255) NOT NULL,
    PRIMARY KEY (offences_id)
    );
CREATE TABLE
    IF NOT EXISTS hours_type
(
    hours_type_id      BIGINT NOT NULL,
    mn_id         BIGINT NOT NULL,
    mn_identifier VARCHAR(255) NOT NULL,
    mn_hours_name VARCHAR(255) NOT NULL,
    PRIMARY KEY (hours_type_id)
    );
CREATE TABLE
    IF NOT EXISTS offences_type_list
(
    offences_type_List_id BIGINT NOT NULL,
    offences_id           BIGINT ,
    offences_other        VARCHAR(255) ,
    PRIMARY KEY (offences_type_List_id,offences_id,offences_other),
    CONSTRAINT fk_employer_sector FOREIGN KEY(offences_id) REFERENCES offences_type
(offences_id)
    );
CREATE TABLE
    IF NOT EXISTS job_source
(
    job_source_id      BIGINT NOT NULL,
    mn_id              BIGINT NOT NULL,
    mn_identifier      VARCHAR(255) NOT NULL,
    mn_job_source_name VARCHAR(255) NOT NULL,
    PRIMARY KEY (job_source_id)
    );
CREATE TABLE
    IF NOT EXISTS job_source_list
(
    job_source_list_id BIGINT NOT NULL,
    job_source_id      BIGINT ,
    PRIMARY KEY (job_source_list_id,job_source_id),
    CONSTRAINT fk_job_source FOREIGN KEY(job_source_id) REFERENCES job_source (job_source_id)
    );
CREATE TABLE
    IF NOT EXISTS job_type
(
    job_type_id      BIGINT NOT NULL,
    mn_id            BIGINT NOT NULL,
    mn_identifier    VARCHAR(255) NOT NULL,
    mn_job_type_name VARCHAR(255) NOT NULL,
    PRIMARY KEY (job_type_id)
    );
CREATE TABLE
    IF NOT EXISTS salary_period
(
    salary_period_id      BIGINT NOT NULL,
    mn_id                 BIGINT NOT NULL,
    mn_identifier         VARCHAR(255) NOT NULL,
    mn_salary_period_name VARCHAR(255) NOT NULL,
    PRIMARY KEY (salary_period_id)
    );
CREATE TABLE
    IF NOT EXISTS work_pattern
(
    work_pattern_id      BIGINT NOT NULL,
    mn_id                BIGINT NOT NULL,
    mn_identifier        VARCHAR(255) NOT NULL,
    mn_work_pattern_name VARCHAR(255) NOT NULL,
    PRIMARY KEY (work_pattern_id)
    );
CREATE TABLE
    IF NOT EXISTS prison_leavers_job
(
    job_id                        BIGINT NOT NULL,
    mn_job_id                     BIGINT NOT NULL,
    salary_period_id              BIGINT NOT NULL,
    work_pattern_id               BIGINT NOT NULL,
    hours_type_id                      BIGINT NOT NULL,
    job_contract_type_id                      BIGINT NOT NULL,
    offences_type_List_id BIGINT NOT NULL,
    offences_id           BIGINT ,
    offences_other        VARCHAR(255) ,
    job_source_list_id BIGINT NOT NULL,
    job_source_id      BIGINT ,
    job_source1_id                 BIGINT NOT NULL,
    job_type_id                   BIGINT NOT NULL,
    base_location_id              BIGINT NOT NULL,
    charity_id                    BIGINT NOT NULL,
    job_contract_id               BIGINT NOT NULL,
    employer_id                   BIGINT NOT NULL,
    employer_sector_id            BIGINT NOT NULL,
    additional_salary_information VARCHAR(255),
    desirable_job_criteria        VARCHAR(255),
    essential_job_criteria        VARCHAR(255),
    closing_date                  VARCHAR(255),
    how_to_apply                  VARCHAR(255),
    job_title                     VARCHAR(255),
    mn_created_by_id              BIGINT NOT NULL,
    created_by                    VARCHAR(255),
    created_date_time             TIMESTAMP,
    posting_date                  VARCHAR(255),
    mn_deleted_by_id              BIGINT NOT NULL,
    deleted_by                    VARCHAR(255),
    deleted_date_time             TIMESTAMP,
    modified_by                   VARCHAR(255),
    modified_date_time            TIMESTAMP,
    national_minimum_wage         BOOLEAN,
    post_code                     VARCHAR(255),
    ring_fenced_job               BOOLEAN,
    rolling_job_oppurtunity       BOOLEAN,
    active_job                    BOOLEAN,
    deleted_job                   BOOLEAN,
    salary_from                   VARCHAR(255),
    salary_to                     VARCHAR(255),
    PRIMARY KEY (job_id),
    CONSTRAINT fk_employer_sector FOREIGN KEY(employer_sector_id) REFERENCES
    employer_work_sector (sector_id),
    CONSTRAINT fk_job_charity FOREIGN KEY(charity_id) REFERENCES job_charity (charity_id),
    CONSTRAINT fk_work_pattern FOREIGN KEY(work_pattern_id) REFERENCES work_pattern
(work_pattern_id),
    CONSTRAINT fk_salary_period FOREIGN KEY(salary_period_id) REFERENCES salary_period
(salary_period_id),
    CONSTRAINT fk_job_source_list FOREIGN KEY(job_source_list_id,job_source_id) REFERENCES job_source_list
(job_source_list_id,job_source_id) ,
    CONSTRAINT fk_job_source FOREIGN KEY(job_source1_id) REFERENCES job_source(job_source_id),
    CONSTRAINT fk_offences_type_list FOREIGN KEY(offences_type_List_id,offences_id,offences_other) REFERENCES
    offences_type_list(offences_type_List_id,offences_id,offences_other) ,
    CONSTRAINT fk_job_type FOREIGN KEY(job_type_id) REFERENCES job_type(job_type_id),
    CONSTRAINT fk_job_contract_type FOREIGN KEY(job_contract_type_id) REFERENCES
    job_contract_type(job_contract_type_id) ,
    CONSTRAINT fk_hours_type FOREIGN KEY(hours_type_id) REFERENCES hours_type(hours_type_id),
    CONSTRAINT fk_base_location FOREIGN KEY(base_location_id) REFERENCES base_location
(base_location_id),
    CONSTRAINT fk_job_employers FOREIGN KEY(employer_id) REFERENCES job_employers(employer_id)
    );