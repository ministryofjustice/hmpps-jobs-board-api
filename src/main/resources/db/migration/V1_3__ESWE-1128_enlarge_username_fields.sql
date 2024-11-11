ALTER TABLE employers
    ALTER COLUMN created_by TYPE VARCHAR(240),
    ALTER COLUMN last_modified_by TYPE VARCHAR(240);
ALTER TABLE jobs
    ALTER COLUMN created_by TYPE VARCHAR(240),
    ALTER COLUMN last_modified_by TYPE VARCHAR(240);
ALTER TABLE postcodes
    ALTER COLUMN created_by TYPE VARCHAR(240),
    ALTER COLUMN last_modified_by TYPE VARCHAR(240);
ALTER TABLE applications
    ALTER COLUMN created_by TYPE VARCHAR(240),
    ALTER COLUMN last_modified_by TYPE VARCHAR(240);

ALTER TABLE revision_info
    ALTER COLUMN created_by TYPE VARCHAR(240);
ALTER TABLE applications_audit
    ALTER COLUMN created_by TYPE VARCHAR(240),
    ALTER COLUMN last_modified_by TYPE VARCHAR(240);
