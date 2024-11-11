ALTER TABLE jobs_expressions_of_interest
    ADD COLUMN created_by VARCHAR(240);
ALTER TABLE jobs_archived
    ADD COLUMN created_by VARCHAR(240);

UPDATE jobs_expressions_of_interest set created_by = 'system' WHERE created_by is NULL;
UPDATE jobs_archived set created_by = 'system' WHERE created_by is NULL;

ALTER TABLE jobs_expressions_of_interest ALTER COLUMN created_by SET NOT NULL;
ALTER TABLE jobs_archived ALTER COLUMN created_by SET NOT NULL;
