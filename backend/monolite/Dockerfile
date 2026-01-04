FROM maven:3.9-eclipse-temurin-25 AS build
WORKDIR /app

COPY target/*.jar app.jar

EXPOSE 8080
CMD ["java", "-jar", "app.jar"]