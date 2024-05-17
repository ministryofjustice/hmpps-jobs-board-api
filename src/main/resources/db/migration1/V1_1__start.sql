DROP TABLE IF EXISTS base_location;
CREATE TABLE base_location (base_location_id BIGINT NOT NULL, mn_id BIGINT NOT NULL, mn_identifier CHARACTER VARYING(255) NOT NULL, mn_sector_name CHARACTER VARYING(255) NOT NULL, PRIMARY KEY (base_location_id));
DROP TABLE IF EXISTS employer_partner;
CREATE TABLE employer_partner (partner_id BIGINT NOT NULL, partner_grade_id BIGINT NOT NULL, mn_id BIGINT NOT NULL, mn_identifier CHARACTER VARYING(255) NOT NULL, mn_partner_name CHARACTER VARYING(255) NOT NULL, PRIMARY KEY (partner_id));
DROP TABLE IF EXISTS employer_partner_grades;
CREATE TABLE employer_partner_grades (partner_grade_id BIGINT NOT NULL, mn_id BIGINT NOT NULL, mn_identifier CHARACTER VARYING(255) NOT NULL, mn_grade CHARACTER VARYING(255) NOT NULL, PRIMARY KEY (partner_grade_id));
DROP TABLE IF EXISTS employer_work_sector;
CREATE TABLE employer_work_sector (sector_id BIGINT NOT NULL, mn_id BIGINT NOT NULL, mn_identifier CHARACTER VARYING(255) NOT NULL, mn_sector_name CHARACTER VARYING(255) NOT NULL, PRIMARY KEY (sector_id));
DROP TABLE IF EXISTS flyway_schema_history;
CREATE TABLE flyway_schema_history (installed_rank INTEGER NOT NULL, version CHARACTER VARYING(50), description CHARACTER VARYING(200) NOT NULL, type CHARACTER VARYING(20) NOT NULL, script CHARACTER VARYING(1000) NOT NULL, checksum INTEGER, installed_by CHARACTER VARYING(100) NOT NULL, installed_on TIMESTAMP(6) WITHOUT TIME ZONE DEFAULT now() NOT NULL, execution_time INTEGER NOT NULL, success BOOLEAN NOT NULL, CONSTRAINT flyway_schema_history_pk PRIMARY KEY (installed_rank));
DROP TABLE IF EXISTS hours_type;
CREATE TABLE hours_type (hours_type_id BIGINT NOT NULL, mn_id BIGINT NOT NULL, mn_identifier CHARACTER VARYING(255) NOT NULL, mn_hours_name CHARACTER VARYING(255) NOT NULL, PRIMARY KEY (hours_type_id));
DROP TABLE IF EXISTS job_charity;
CREATE TABLE job_charity (charity_id BIGINT NOT NULL, charity_name_name CHARACTER VARYING(255), charity_bio CHARACTER VARYING(255), created_by CHARACTER VARYING(255), created_date_time TIMESTAMP(6) WITHOUT TIME ZONE, modified_by CHARACTER VARYING(255), modified_date_time TIMESTAMP(6) WITHOUT TIME ZONE, image_id BIGINT NOT NULL, PRIMARY KEY (charity_id));
DROP TABLE IF EXISTS job_contract_type;
CREATE TABLE job_contract_type (job_contract_type_id BIGINT NOT NULL, mn_id BIGINT NOT NULL, mn_identifier CHARACTER VARYING(255) NOT NULL, mn_job_contract_name CHARACTER VARYING(255) NOT NULL, PRIMARY KEY (job_contract_type_id));
DROP TABLE IF EXISTS job_employers;
CREATE TABLE job_employers (employer_id BIGINT NOT NULL, employer_name CHARACTER VARYING(255), employer_bio CHARACTER VARYING(255), created_by CHARACTER VARYING(255), created_date_time TIMESTAMP(6) WITHOUT TIME ZONE, modified_by CHARACTER VARYING(255), modified_date_time TIMESTAMP(6) WITHOUT TIME ZONE, sector_id BIGINT NOT NULL, partner_id BIGINT NOT NULL, image_id BIGINT NOT NULL, post_code CHARACTER VARYING(255), PRIMARY KEY (employer_id));
DROP TABLE IF EXISTS job_image;
CREATE TABLE job_image (image_id BIGINT NOT NULL, mn_id BIGINT NOT NULL, image_path CHARACTER VARYING(255) NOT NULL, PRIMARY KEY (image_id));
DROP TABLE IF EXISTS job_source;
CREATE TABLE job_source (job_source_id BIGINT NOT NULL, mn_id BIGINT NOT NULL, mn_identifier CHARACTER VARYING(255) NOT NULL, mn_job_source_name CHARACTER VARYING(255) NOT NULL, PRIMARY KEY (job_source_id));
DROP TABLE IF EXISTS job_source_list;
CREATE TABLE job_source_list (id BIGINT NOT NULL, job_source_id BIGINT NOT NULL, PRIMARY KEY (id, job_source_id));
DROP TABLE IF EXISTS job_type;
CREATE TABLE job_type (job_type_id BIGINT NOT NULL, mn_id BIGINT NOT NULL, mn_identifier CHARACTER VARYING(255) NOT NULL, mn_job_type_name CHARACTER VARYING(255) NOT NULL, PRIMARY KEY (job_type_id));
DROP TABLE IF EXISTS offences_type;
CREATE TABLE offences_type (offences_id BIGINT NOT NULL, mn_id BIGINT NOT NULL, mn_identifier CHARACTER VARYING(255) NOT NULL, mn_offences_name CHARACTER VARYING(255) NOT NULL, PRIMARY KEY (offences_id));
DROP TABLE IF EXISTS offences_type_list;
CREATE TABLE offences_type_list (offences_type_list_id BIGINT NOT NULL, offences_type_list_offences_id BIGINT NOT NULL, offences_other CHARACTER VARYING(255) NOT NULL, offences_type BIGINT NOT NULL, offences_offences_id BIGINT, PRIMARY KEY (offences_type_list_id, offences_type_list_offences_id, offences_other));
DROP TABLE IF EXISTS prison_leavers_job;
CREATE TABLE prison_leavers_job (job_id BIGINT NOT NULL, mn_job_id BIGINT NOT NULL, salary_period_id BIGINT NOT NULL, work_pattern_id BIGINT NOT NULL, hours_type_id BIGINT NOT NULL, job_contract_type_id BIGINT NOT NULL, job_type_id BIGINT NOT NULL, base_location_id BIGINT NOT NULL, job_contract_id BIGINT NOT NULL, employer_id BIGINT NOT NULL, employer_sector_id BIGINT NOT NULL, additional_salary_information CHARACTER VARYING(255), desirable_job_criteria CHARACTER VARYING(255), essential_job_criteria CHARACTER VARYING(255), closing_date CHARACTER VARYING(255), how_to_apply CHARACTER VARYING(255), job_title CHARACTER VARYING(255), mn_created_by_id BIGINT NOT NULL, created_by CHARACTER VARYING(255), created_date_time TIMESTAMP(6) WITHOUT TIME ZONE, posting_date CHARACTER VARYING(255), mn_deleted_by_id BIGINT NOT NULL, deleted_by CHARACTER VARYING(255), deleted_date_time TIMESTAMP(6) WITHOUT TIME ZONE, modified_by CHARACTER VARYING(255), modified_date_time TIMESTAMP(6) WITHOUT TIME ZONE, national_minimum_wage BOOLEAN, post_code CHARACTER VARYING(255), ring_fenced_job BOOLEAN, rolling_job_oppurtunity BOOLEAN, active_job BOOLEAN, deleted_job BOOLEAN, salary_from CHARACTER VARYING(255), salary_to CHARACTER VARYING(255), type_of_work CHARACTER VARYING(255), PRIMARY KEY (job_id));
DROP TABLE IF EXISTS salary_period;
CREATE TABLE salary_period (salary_period_id BIGINT NOT NULL, mn_id BIGINT NOT NULL, mn_identifier CHARACTER VARYING(255) NOT NULL, mn_salary_period_name CHARACTER VARYING(255) NOT NULL, PRIMARY KEY (salary_period_id));
DROP TABLE IF EXISTS work_pattern;
CREATE TABLE work_pattern (work_pattern_id BIGINT NOT NULL, mn_id BIGINT NOT NULL, mn_identifier CHARACTER VARYING(255) NOT NULL, mn_work_pattern_name CHARACTER VARYING(255) NOT NULL, PRIMARY KEY (work_pattern_id));
ALTER TABLE "employer_partner" ADD CONSTRAINT fk_employer_partner_grades FOREIGN KEY ("partner_grade_id") REFERENCES "employer_partner_grades" ("partner_grade_id");
ALTER TABLE "job_charity" ADD CONSTRAINT fk_employer_image FOREIGN KEY ("image_id") REFERENCES "job_image" ("image_id");
ALTER TABLE "job_employers" ADD CONSTRAINT fk_employer_sector FOREIGN KEY ("sector_id") REFERENCES "employer_work_sector" ("sector_id");
ALTER TABLE "job_employers" ADD CONSTRAINT fk_employer_partner FOREIGN KEY ("partner_id") REFERENCES "employer_partner" ("partner_id");
ALTER TABLE "job_employers" ADD CONSTRAINT fk_employer_image FOREIGN KEY ("image_id") REFERENCES "job_image" ("image_id");
ALTER TABLE "job_source_list" ADD CONSTRAINT fk_job_source FOREIGN KEY ("job_source_id") REFERENCES "job_source" ("job_source_id");
ALTER TABLE "offences_type_list" ADD CONSTRAINT fk_employer_sector FOREIGN KEY ("offences_type_list_offences_id") REFERENCES "offences_type" ("offences_id");
ALTER TABLE "prison_leavers_job" ADD CONSTRAINT fk_employer_sector FOREIGN KEY ("employer_sector_id") REFERENCES "employer_work_sector" ("sector_id");
ALTER TABLE "prison_leavers_job" ADD CONSTRAINT fk_work_pattern FOREIGN KEY ("work_pattern_id") REFERENCES "work_pattern" ("work_pattern_id");
ALTER TABLE "prison_leavers_job" ADD CONSTRAINT fk_salary_period FOREIGN KEY ("salary_period_id") REFERENCES "salary_period" ("salary_period_id");
ALTER TABLE "prison_leavers_job" ADD CONSTRAINT fk_job_type FOREIGN KEY ("job_type_id") REFERENCES "job_type" ("job_type_id");
ALTER TABLE "prison_leavers_job" ADD CONSTRAINT fk_job_contract_type FOREIGN KEY ("job_contract_type_id") REFERENCES "job_contract_type" ("job_contract_type_id");
ALTER TABLE "prison_leavers_job" ADD CONSTRAINT fk_hours_type FOREIGN KEY ("hours_type_id") REFERENCES "hours_type" ("hours_type_id");
ALTER TABLE "prison_leavers_job" ADD CONSTRAINT fk_base_location FOREIGN KEY ("base_location_id") REFERENCES "base_location" ("base_location_id");
ALTER TABLE "prison_leavers_job" ADD CONSTRAINT fk_job_employers FOREIGN KEY ("employer_id") REFERENCES "job_employers" ("employer_id");
