CREATE INDEX IF NOT EXISTS applications_audit_prison_idx
    ON applications_audit (prison_id, last_modified_at);
