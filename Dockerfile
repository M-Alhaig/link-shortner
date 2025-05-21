# Use an official OpenJDK runtime as a parent image
FROM openjdk:21-jdk

# Set the working directory inside the container
WORKDIR /app

# Copy the built jar file into the container
COPY target/link-shortener-0.0.1-SNAPSHOT.jar app.jar

# Expose the port your app listens on (adjust if needed)
EXPOSE 8080

# Command to run the jar
ENTRYPOINT ["java", "-jar", "app.jar"]
