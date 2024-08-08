create table if not exists employers (
    id varchar(36) primary key,
    name varchar(255) NOT NULL,
    description varchar(500) NOT NULL,
    sector varchar(255) NOT NULL,
    status varchar(255) NOT NULL,
    created_at timestamp(6) NOT NULL
);

create table if not exists jobs(
    id varchar(36) primary key,
    employer_id varchar(36) not null,
    title varchar(50) not null,
    sector varchar(255) not null,
    industry_sector varchar(255) not null,
    number_of_vacancies int not null,
    source_primary varchar(255) not null,
    source_secondary varchar(255),
    charity_name varchar(100) not null,
    post_code varchar(255) not null,
    salary_from numeric(10, 2) not null,
    salary_to numeric(10, 2),
    salary_period varchar(255) not null,
    additional_salary_information varchar(100),
    is_paying_at_least_national_minimum_wage boolean not null,
    work_pattern varchar(255) not null,
    hours_per_week varchar(255) not null,
    contract_type varchar(255) not null,
    base_location varchar(255) not null,
    essential_criteria varchar(1000) not null,
    desirable_criteria varchar(1000),
    description varchar(3000) not null,
    offence_exclusions varchar(355) not null,
    is_rolling_opportunity boolean not null,
    closing_date date,
    is_only_for_prison_leavers boolean not null,
    start_date date,
    how_to_apply varchar(1000) not null,
    supporting_documentation_required varchar(255),
    supporting_documentation_details varchar(200),
    created_at timestamp(6) NOT NULL,
    modified_at timestamp(6) NOT NULL,

    CONSTRAINT fk_employer
        FOREIGN KEY(employer_id)
        REFERENCES Employers(id)
        ON DELETE CASCADE
);
