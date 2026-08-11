FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /workspace

COPY pom.xml .
COPY shared-kernel shared-kernel
COPY microservices/order/order-contract microservices/order/order-contract
COPY microservices/payment/payment-contract microservices/payment/payment-contract
COPY microservices/fulfillment/fulfillment-contract microservices/fulfillment/fulfillment-contract
COPY microservices/email/email-contract microservices/email/email-contract
COPY microservices/orchestrator microservices/orchestrator

RUN mvn -q -pl microservices/orchestrator/orchestrator-api -am package -DskipTests

FROM eclipse-temurin:21-jre
WORKDIR /app
EXPOSE 8080
COPY --from=build /workspace/microservices/orchestrator/orchestrator-api/target/orchestrator-api.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
