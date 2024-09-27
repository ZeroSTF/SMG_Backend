# Use an official OpenJDK runtime as a parent image
FROM openjdk:17-jdk-alpine

# Set the working directory inside the container
WORKDIR /app

# Copy the packaged Spring Boot JAR file into the container
COPY target/SMG-0.9.0.jar /app/SMG-0.9.0.jar

# Expose the port that the Spring Boot application listens on
EXPOSE 8080

# Run the Spring Boot application
ENTRYPOINT ["java", "-jar", "/app/SMG-0.9.0.jar"]
