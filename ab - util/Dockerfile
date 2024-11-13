FROM maven:3.9.9-eclipse-temurin-21-alpine
COPY pom.xml .
COPY ./src ./src
RUN mvn clean install -Dmaven.test.skip=true