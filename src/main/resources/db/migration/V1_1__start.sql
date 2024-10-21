CREATE TABLE IF NOT EXISTS employers
(
    id               VARCHAR(36) PRIMARY KEY,
    name             VARCHAR(255) NOT NULL,
    description      VARCHAR(500) NOT NULL,
    sector           VARCHAR(255) NOT NULL,
    status           VARCHAR(255) NOT NULL,
    created_by       VARCHAR(30)  NOT NULL,
    last_modified_by VARCHAR(30)  NOT NULL,
    created_at       TIMESTAMP(6) NOT NULL,
    last_modified_at TIMESTAMP(6) NOT NULL
);

CREATE TABLE IF NOT EXISTS jobs
(
    id                                       VARCHAR(36) primary key,
    employer_id                              VARCHAR(36)    NOT NULL,
    title                                    VARCHAR(50)    NOT NULL,
    sector                                   VARCHAR(255)   NOT NULL,
    industry_sector                          VARCHAR(255)   NOT NULL,
    number_of_vacancies                      INT            NOT NULL,
    source_primary                           VARCHAR(255)   NOT NULL,
    source_secondary                         VARCHAR(255),
    charity_name                             VARCHAR(100),
    postcode                                 VARCHAR(7)     NOT NULL,
    salary_from                              NUMERIC(10, 2) NOT NULL,
    salary_to                                NUMERIC(10, 2),
    salary_period                            VARCHAR(255)   NOT NULL,
    additional_salary_information            VARCHAR(100),
    is_paying_at_least_national_minimum_wage BOOLEAN        NOT NULL,
    work_pattern                             VARCHAR(255)   NOT NULL,
    hours_per_week                           VARCHAR(255)   NOT NULL,
    contract_type                            VARCHAR(255)   NOT NULL,
    base_location                            VARCHAR(255),
    essential_criteria                       VARCHAR(1000)  NOT NULL,
    desirable_criteria                       VARCHAR(1000),
    description                              VARCHAR(3000)  NOT NULL,
    offence_exclusions                       VARCHAR(355)   NOT NULL,
    offence_exclusions_details               VARCHAR(500),
    is_rolling_opportunity                   BOOLEAN        NOT NULL,
    closing_date                             DATE,
    is_only_for_prison_leavers               BOOLEAN        NOT NULL,
    start_date                               DATE,
    how_to_apply                             VARCHAR(1000)  NOT NULL,
    supporting_documentation_required        VARCHAR(255),
    supporting_documentation_details         VARCHAR(200),
    created_by                               VARCHAR(30)    NOT NULL,
    last_modified_by                         VARCHAR(30)    NOT NULL,
    created_at                               TIMESTAMP(6)   NOT NULL,
    last_modified_at                         TIMESTAMP(6)   NOT NULL,

    CONSTRAINT fk_employer
        FOREIGN KEY (employer_id)
            REFERENCES Employers (id)
            ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS jobs_expressions_of_interest
(
    job_id        VARCHAR(36)  NOT NULL,
    prison_number VARCHAR(7)   NOT NULL,
    created_at    TIMESTAMP(6) NOT NULL,

    PRIMARY KEY (job_id, prison_number),
    CONSTRAINT fk_jobs
        FOREIGN KEY (job_id)
            REFERENCES jobs (id)
            ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS jobs_archived
(
    job_id        VARCHAR(36)  NOT NULL,
    prison_number VARCHAR(7)   NOT NULL,
    created_at    TIMESTAMP(6) NOT NULL,

    PRIMARY KEY (job_id, prison_number),
    CONSTRAINT fk_jobs
        FOREIGN KEY (job_id)
            REFERENCES jobs (id)
            ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS postcodes
(
    id               VARCHAR(36) PRIMARY KEY,
    code             VARCHAR(7)    NOT NULL,
    x_coordinate     NUMERIC(6, 2) NOT NULL,
    y_coordinate     NUMERIC(7, 2) NOT NULL,
    created_by       VARCHAR(30)   NOT NULL,
    last_modified_by VARCHAR(30)   NOT NULL,
    created_at       TIMESTAMP(6)  NOT NULL,
    last_modified_at TIMESTAMP(6)  NOT NULL
);

CREATE TABLE IF NOT EXISTS applications
(
    id                     VARCHAR(36)  NOT NULL,
    job_id                 VARCHAR(36)  NOT NULL,
    prison_number          VARCHAR(7)   NOT NULL,
    prison_id              VARCHAR(3)   NOT NULL,
    first_name             VARCHAR(100),
    last_name              VARCHAR(100),
    status                 VARCHAR(25)  NOT NULL,
    additional_information VARCHAR(500),
    created_by             VARCHAR(30)  NOT NULL,
    last_modified_by       VARCHAR(30)  NOT NULL,
    created_at             TIMESTAMP(6) NOT NULL,
    last_modified_at       TIMESTAMP(6) NOT NULL,

    PRIMARY KEY (id),
    CONSTRAINT fk_jobs
        FOREIGN KEY (job_id)
            REFERENCES jobs (id)
            ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS revision_info
(
    rev_number BIGINT GENERATED BY DEFAULT as IDENTITY,
    rev_time   BIGINT,
    created_by VARCHAR(30) NOT NULL,

    PRIMARY KEY (rev_number)
);

CREATE TABLE IF NOT EXISTS applications_audit
(
    rev_number             BIGINT       NOT NULL,
    rev_type               SMALLINT,
    id                     VARCHAR(36)  NOT NULL,
    job_id                 VARCHAR(36)  NOT NULL,
    prison_number          VARCHAR(7)   NOT NULL,
    prison_id              VARCHAR(3)   NOT NULL,
    first_name             VARCHAR(100),
    last_name              VARCHAR(100),
    status                 VARCHAR(25)  NOT NULL,
    additional_information VARCHAR(500),
    created_by             VARCHAR(30)  NOT NULL,
    last_modified_by       VARCHAR(30)  NOT NULL,
    created_at             TIMESTAMP(6) NOT NULL,
    last_modified_at       TIMESTAMP(6) NOT NULL,

    PRIMARY KEY (id, rev_number)
);