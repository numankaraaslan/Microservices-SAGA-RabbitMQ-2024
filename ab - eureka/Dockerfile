FROM maven:3.9.9-eclipse-temurin-21-alpine as stage1
COPY pom.xml .
COPY ./src ./src
RUN mvn clean install -Dmaven.test.skip=true
FROM eclipse-temurin:21.0.1_12-jre-jammy
COPY --from=stage1 target/*.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar", "--logging.level.root=WARN"]