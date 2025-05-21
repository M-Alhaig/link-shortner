# Build stage
FROM maven:3.8-openjdk-21 AS build
WORKDIR /app
COPY . .
RUN ./mvnw clean package -DskipTests

# Run stage
FROM openjdk:21-jdk
WORKDIR /app
COPY --from=build /app/target/link-shortener-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]