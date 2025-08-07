FROM openjdk:17-jdk-slim

WORKDIR /app

COPY mvnw .
COPY mvnw.cmd .
COPY pom.xml .
COPY src src

RUN chmod +x mvnw
RUN ./mvnw clean package -DskipTests

EXPOSE 8081

ENTRYPOINT ["java", "-jar", "target/Product-service-0.0.1-SNAPSHOT.jar"] 