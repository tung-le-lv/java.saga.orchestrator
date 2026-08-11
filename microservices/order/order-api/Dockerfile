FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /workspace

COPY pom.xml .
COPY shared-kernel/shared-domain/pom.xml shared-kernel/shared-domain/pom.xml
COPY shared-kernel/shared-integration-messages/pom.xml shared-kernel/shared-integration-messages/pom.xml
COPY shared-kernel/shared-application/pom.xml shared-kernel/shared-application/pom.xml
COPY shared-kernel/shared-mongodb/pom.xml shared-kernel/shared-mongodb/pom.xml
COPY shared-kernel/shared-messaging/pom.xml shared-kernel/shared-messaging/pom.xml
COPY microservices/order/order-contract/pom.xml microservices/order/order-contract/pom.xml
COPY microservices/order/order-domain/pom.xml microservices/order/order-domain/pom.xml
COPY microservices/order/order-application/pom.xml microservices/order/order-application/pom.xml
COPY microservices/order/order-infrastructure/pom.xml microservices/order/order-infrastructure/pom.xml
COPY microservices/order/order-api/pom.xml microservices/order/order-api/pom.xml

COPY shared-kernel shared-kernel
COPY microservices/order microservices/order

RUN mvn -q -pl microservices/order/order-api -am package -DskipTests

FROM eclipse-temurin:21-jre
WORKDIR /app
EXPOSE 8080
COPY --from=build /workspace/microservices/order/order-api/target/order-api.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
