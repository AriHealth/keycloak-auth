# keycloak-auth

[![Build Status](https://travis-ci.org/AriHealth/keycloak-auth.svg?branch=master)](https://travis-ci.org/AriHealth/keycloak-auth) 
[![codecov.io](https://codecov.io/gh/AriHealth/keycloak-auth/branch/master/graphs/badge.svg)](http://codecov.io/gh/AriHealth/keycloak-auth)
[![Quality Gate](https://sonarcloud.io/api/project_badges/measure?project=net.atos.ari:auth&metric=alert_status)](https://sonarcloud.io/dashboard/index/net.atos.ari:auth)
[![Docker Build](https://img.shields.io/docker/cloud/build/arihealth/oauth-easy)](https://cloud.docker.com/u/arihealth/repository/docker/arihealth/oauth-easy)
[![Docker Pulls](https://img.shields.io/docker/pulls/arihealth/oauth-easy)](https://cloud.docker.com/u/arihealth/repository/docker/arihealth/oauth-easy)
[![License](https://img.shields.io/badge/License-Apache%202.0-green.svg)](https://opensource.org/licenses/Apache-2.0)

## Description

OAuth2 authentication to manage Keycloak tokens (it needs a Keycloak url, realm and client_id to work) to allow login based on user and password. It exposes an API with these operations:

- [POST] login
- [GET] user

## Technology

- Java 8
- Maven for Java dependency management
- Spring Boot 
- keycloak server
- Lombok for the models

## Functionalities

- Login, get a token given user name and password
- User, get the username given the token

## How to deploy

Compile and package the project with

```
mvn clean package
```

and execute

```
java -jar target/auth.jar
```

It can also be run as:

```
mvn spring-boot:run
```

Go to your browser and type http://localhost:8081/swagger-ui.html

Use the application properties according to your Keycloak server configuration.

## Keycloak configuration

- Create a realm for your project
- Create a client in the realm
	- Valid Redirect URIs: Put "http://localhost:9090/*"
- Create a valid role
- Create a new user, set a password

## Environment variables

    KEYCLOAK_URL=
    KEYCLOAK_REALM=
    KEYCLOAK_CLIENT_ID=

## Docker deployment

The Keycloak parameters are configured using environment variables, that are referenced in the `application.yml` file. Assuming that realm="test", client_id="test" and url="localhost:9090":

```
docker run --name auth -d -e KEYCLOAK_URL=http://localhost:9090/auth -e KEYCLOAK_REALM=test -e KEYCLOAK_CLIENT_ID=test health/auth
```

Logging can be also configured using `LOGGING_FOLDER` and sharing a volume (this is useful for example for [ELK](https://www.elastic.co/elk-stack) processing). The level of the logging can be configured with `LOGGING_MODE` (dev|prod):

```
docker run --name auth -d -v /home/docker/log/test:/log/test -e KEYCLOAK_URL=http://localhost:9090/auth -e KEYCLOAK_REALM=test -e KEYCLOAK_CLIENT_ID=test -e LOGGING_FOLDER=/log/test -e LOGGING_MODE=dev health/auth
```

## License

Apache 2.0

By downloading this software, the downloader agrees with the specified terms and conditions of the License Agreement and the particularities of the license provided.
