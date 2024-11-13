FROM microservices-saga-rabbitmq-2024-abutil AS util-stage
FROM maven:3.9.9-eclipse-temurin-21-alpine as stage1
RUN mkdir -p /root/.m2/repository/com/aldimbilet/util
COPY --from=util-stage /target/abutil.jar /root/.m2/repository/com/aldimbilet/util/1.0.0/util-1.0.0.jar
COPY pom.xml .
COPY ./src ./src
RUN mvn clean install -Dmaven.test.skip=true
FROM eclipse-temurin:21.0.1_12-jre-jammy
COPY --from=stage1 target/*.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar", "--logging.level.root=WARN", "--spring.rabbitmq.host=rabbitmq", "--gateway.adress.userservice=http://gateway:4441/user/", "--gateway.adress.activityservice=http://gateway:4441/act/", "--gateway.adress.paymentservice=http://gateway:4441/pay/"]