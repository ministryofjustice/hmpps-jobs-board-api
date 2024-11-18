ALTER TABLE jobs_expressions_of_interest
    ADD COLUMN IF NOT EXISTS created_by VARCHAR(240) NOT NULL DEFAULT 'system';
ALTER TABLE jobs_archived
    ADD COLUMN IF NOT EXISTS created_by VARCHAR(240) NOT NULL DEFAULT 'system';

ALTER TABLE jobs_expressions_of_interest ALTER COLUMN created_by DROP DEFAULT;
ALTER TABLE jobs_archived ALTER COLUMN created_by DROP DEFAULT;
