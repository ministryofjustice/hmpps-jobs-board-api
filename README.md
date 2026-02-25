# hmpps-jobs-board-api
[![Ministry of Justice Repository Compliance Badge](https://github-community.service.justice.gov.uk/repository-standards/api/hmpps-jobs-board-api/badge?style=flat)](https://github-community.service.justice.gov.uk/repository-standards/hmpps-jobs-board-api)
[![Docker Repository on ghcr](https://img.shields.io/badge/ghcr.io-repository-2496ED.svg?logo=docker)](https://ghcr.io/ministryofjustice/hmpps-jobs-board-api)
[![API docs](https://img.shields.io/badge/API_docs_-view-85EA2D.svg?logo=swagger)](https://jobs-board-api-dev.hmpps.service.justice.gov.uk/swagger-ui/index.html)
[![Pipeline [test -> build -> deploy]](https://github.com/ministryofjustice/hmpps-jobs-board-api/actions/workflows/pipeline.yml/badge.svg?branch=main)](https://github.com/ministryofjustice/hmpps-jobs-board-api/actions/workflows/pipeline.yml)
# About
The **Match Jobs and Manage Applications** - Jobs Board API provides backend services _Jobs Uploads_, _Employers Uploads_ and _Candidate Matching_.

* The product page on Developer Portal: [Match Jobs and Manage Applications](https://developer-portal.hmpps.service.justice.gov.uk/products/candidate-matching-1)
* The high level design on Confluence: [Match Jobs & Manage Applications - HLD](https://dsdmoj.atlassian.net/wiki/x/34NiJgE)

## Team
This backend service is developed and supported by `Education Skills & Work` team. They can be contacted via `#education-skills-work-employment-dev` on Slack.

## Healthiness
The integration service has a `/health` endpoint which indicates the service is up and running.

# Instructions

## Running the application locally
This backend application depends on several services to run.

| Dependency    | Description                                              | Default                              | Override Env Var                                                                  |
|---------------|----------------------------------------------------------|--------------------------------------|-----------------------------------------------------------------------------------|
| hmpps-auth    | OAuth2 API server for authenticating requests            |                                      | `API_BASE_URL_OAUTH`                                                              |
| OS Places API | OS Places API for resolving postcodes                    | `https://api.os.uk/search/places/v1` | `OS_PLACES_API_URL`                                                               |
| Database      | Database server (`postgres` on local, `RDS` on live env) |                                      | `DATABASE_NAME`, `DATABASE_ENDPOINT`, `DATABASE_USERNAME` and `DATABASE_PASSWORD` |
|

### Environment variables
Defining env var for *local* run

| Env. var.           | description                                              |
|---------------------|----------------------------------------------------------|
| `DATABASE_ENDPOINT` | Database Endpoint (address with port)                    |
| `DATABASE_NAME`     | Database Name                                            |
| `DATABASE_USERNAME` | Database user's username                                 |
| `DATABASE_PASSWORD` | Database user's password                                 |
| `OS_PLACES_API_KEY` | API access key for OS Places API (per given environment) |
_*_ These values can be obtained from k8s secrets on `dev` env.

* Run with the Spring profile `dev` on local
  * Set active profile via this environmental variable `spring.profiles.active=dev` or `SPRING_PROFILES_ACTIVE=dev`
* Run with the Spring profile `local` group on local
  * Set active profile to `local`: `spring.profiles.active=local` or `SPRING_PROFILES_ACTIVE=local`
  * The `local` group will utilise `localstack` for Integration features with message queue (`SQS`) 
* API Spec:
    * Goto `http://localhost:8080/swagger-ui/index.html` to explore the OpenAPI specifications
* Checking endpoints
    * Goto `http://localhost:8080/health` to check the service is up and running

### Running with Docker
* Define/Override env var.: define a `.env` file with () required env var from above; [Syntax of .env file](https://docs.docker.com/compose/how-tos/environment-variables/variable-interpolation/#env-file-syntax)

  * provide API key only
    ```dotenv
    OS_PLACES_API_KEY=<API-ACCESS-KEY>
    PRODUCT_ID=DPS015
    ```
  * Specify API key and override Auth URL 
    ```dotenv
    OS_PLACES_API_KEY=<API-ACCESS-KEY>
    API_BASE_URL_OAUTH=https://sign-in-dev.hmpps.service.justice.gov.uk/auth
    PRODUCT_ID=DPS015
    ```
  * Or use `local` profile group
    ```dotenv
    SPRING_PROFILES_ACTIVE=local
    OS_PLACES_API_KEY=<API-ACCESS-KEY>
    PRODUCT_ID=DPS015
    ```
* Run this at CLI
  ```bash
  docker compose pull && docker compose up -d
  ```

will build the application and run it with a `PostgreSQL` database and `localstack` within local docker.

### Running the application in Intellij
* Run this at CLI
  ```bash
  docker compose pull && docker compose up --scale hmpps-jobs-board-api=0 -d
  ```
* will just start docker instance of `PostgreSQL` database and `localstack`. The application should then be started with a `dev` or `local` active profile
in Intellij. 
  * supply required env var, e.g.
    * `spring.profiles.active`=`dev`;`os.places.api.key`=`<API-ACCESS-KEY>`
    * `spring.profiles.active`=`local`;`os.places.api.key`=`<API-ACCESS-KEY>`
