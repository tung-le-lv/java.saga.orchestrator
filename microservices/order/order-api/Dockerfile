FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /workspace

COPY pom.xml .
COPY shared-kernel shared-kernel
COPY microservices microservices

RUN mvn -q -pl microservices/order/order-api -am package -DskipTests

FROM eclipse-temurin:21-jre
WORKDIR /app
EXPOSE 8080
COPY --from=build /workspace/microservices/order/order-api/target/order-api.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
