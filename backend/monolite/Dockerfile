FROM maven:3.9-eclipse-temurin-21 AS build

ARG SERVICE_NAME

WORKDIR /build


COPY backend/${SERVICE_NAME}/pom.xml .
COPY backend/config ./../config
RUN mvn -B dependency:go-offline

COPY backend/${SERVICE_NAME}/src ./src
RUN mvn clean verify -DskipTests

FROM eclipse-temurin:21-jre

WORKDIR /app

COPY --from=build /build/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]