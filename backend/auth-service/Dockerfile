
FROM maven:3.9.6-eclipse-temurin-21 AS build

ARG SERVICE_NAME
WORKDIR /app

COPY config ./config

COPY backend/${SERVICE_NAME}/auth-api/pom.xml backend/${SERVICE_NAME}/auth-api/
COPY backend/${SERVICE_NAME}/auth-core/pom.xml backend/${SERVICE_NAME}/auth-core/


WORKDIR /app/backend/${SERVICE_NAME}/auth-api
RUN mvn -B dependency:go-offline


WORKDIR /app
COPY backend/${SERVICE_NAME}/auth-api/src backend/${SERVICE_NAME}/auth-api/src
COPY backend/${SERVICE_NAME}/auth-core/src backend/${SERVICE_NAME}/auth-core/src

WORKDIR /app/backend/${SERVICE_NAME}/auth-api
RUN mvn clean install -DskipTests

WORKDIR /app/backend/${SERVICE_NAME}/auth-core
RUN mvn clean package -DskipTests

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
ARG SERVICE_NAME

COPY --from=build /app/backend/${SERVICE_NAME}/auth-core/target/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]