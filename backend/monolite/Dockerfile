FROM maven:3.9-eclipse-temurin-25 AS build
WORKDIR /app

COPY backend/pom.xml ./pom.xml
COPY backend/monolite/pom.xml ./monolite/pom.xml

COPY backend/monolite/src ./monolite/src

RUN mvn clean package -DskipTests -f pom.xml
