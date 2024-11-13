FROM maven:3.9.9-eclipse-temurin-21-alpine AS stage1
COPY pom.xml .
COPY ./src ./src
RUN mvn clean install -Dmaven.test.skip=true
FROM eclipse-temurin:21.0.1_12-jre-jammy
COPY --from=stage1 target/*.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar", "--logging.level.root=WARN", "--spring.cloud.config.uri=http://config-server:8888", "--eureka.client.service-url.defaultZone=http://aldimbilet:eureka@eureka:8761/eureka"]