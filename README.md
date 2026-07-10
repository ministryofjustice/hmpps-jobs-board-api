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
This backend application is developed and supported by `Education Skills and Work` team. They can be contacted via `#education-skills-work-employment-dev` on Slack.

## Healthiness
This backend application has a `/health` endpoint which indicates the service is up and running.

# Instructions

## Running the application locally
This backend application depends on several services to run.

| Dependency    | Description                                              | Default                              | Override Env Var                                                                  |
|---------------|----------------------------------------------------------|--------------------------------------|-----------------------------------------------------------------------------------|
| hmpps-auth    | OAuth2 API server for authenticating requests            |                                      | `API_BASE_URL_OAUTH`                                                              |
| OS Places API | OS Places API for resolving postcodes                    | `https://api.os.uk/search/places/v1` | `OS_PLACES_API_URL`                                                               |
| Database      | Database server (`postgres` on local, `RDS` on live env) |                                      | `DATABASE_NAME`, `DATABASE_ENDPOINT`, `DATABASE_USERNAME` and `DATABASE_PASSWORD` |


### Preparation
Obtain API client credentials
- populate those value from kubernetes secrets `hmpps-jobs-board-api`.
  ```shell
  kubectl -n hmpps-jobs-board-dev get secret hmpps-jobs-board-api -o json | jq '.data | map_values(@base64d)' 
  ```
- fill in the OS API key in these files: `OS_PLACES_API_KEY`
    - `.env` for running outside docker
    - `.env.docker` for running in docker

---
### Running with docker compose
The easiest way to run the app is to use docker compose to create the service and all dependencies.
1. Prepare `.env.docker` (from `.env.docker.sample`)
    ```shell
    cp .env.docker.sample .env.docker
    ```
    - fill in the API key in `.env.docker`
      see above to obtain these
2. Then run
   ```shell
   docker compose --profile api up
   ```
   will run the application (from latest image) and PostgreSQL within a local docker instance.
3. Check if application is up and running
    * See `http://localhost:8080/health` to check the app is running.
    * See `http://localhost:8080/swagger-ui/index.html` to explore the OpenAPI spec document.
    * See `http://localhost:8080/info` to check the app info

It connects HMPPS Auth and other upstream APIs in `dev` environment. Thus, a set of valid dev API clients are required to run the application.

---
### Running the application in IntelliJ
1. Prepare `.env` (from `.env.local.sample`)
    ```shell
    cp .env.local.sample .env
    ```
    - fill in the API key in `.env`:
      see above to obtain these
2. Run this
    ```shell
   docker compose up -d 
    ```
    * will start dependencies only without the API application
    * `-d` for detached run
3. Run `bootRun` with  `.env` file prepared above
    * either IntelliJ
        - run `bootRun` with `EnvFile` plugin
        - add `.env`
        - enable integrations
    * or Gradle wrapper
      ```shell
      export $(grep -v '^#' .env | xargs)
      ./gradlew bootRun
      ```

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
* API Spec:
    * Goto `http://localhost:8080/swagger-ui/index.html` to explore the OpenAPI specifications
* Checking endpoints
    * Goto `http://localhost:8080/health` to check the service is up and running

## Run docker image on local

### Build a local docker image
1. Build the app jar
2. Copy jar to project root
3. Build docker image

```shell
BUILD_NUMBER=1_0_0 ./gradlew clean assemble && cp ./build/libs/*.jar .
```
```shell
BUILD_NUMBER=1_0_0 docker build --build-arg BUILD_NUMBER=$BUILD_NUMBER . -t "hmpps-jobs-board-api:local"
```
### Run a local docker image
Set these in your env file (with actual OS API key)
* In `.env.docker.local` 
    ```dotenv
    SPRING_PROFILES_ACTIVE=dev
    PRODUCT_ID=DPS015
    HMPPS_SAR_ADDITIONALACCESSROLE=ROLE_EDUCATION_WORK_PLAN_VIEW
    # `host.docker.internal` (instead of `localhost`) for connecting the image to local DB of host 
    SPRING_DATASOURCE_URL=jdbc:postgresql://host.docker.internal:5432/job-board
    # ================================ SECRETS ================================
    ## get it from secrets: kubectl -n hmpps-jobs-board-dev get secret hmpps-jobs-board-api -o json | jq '.data | map_values(@base64d)'
    OS_PLACES_API_KEY=
    ```

then run this
```shell
docker run --name hmpps-jobs-board-api-app --env-file .env.docker.local -p 8080:8080 -d "hmpps-jobs-board-api:local"
```
