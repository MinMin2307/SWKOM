# Use an official OpenJDK runtime as a parent image
FROM openjdk:17-jdk-alpine

# Set the working directory in the container
WORKDIR /app

# Copy the built jar file into the container
COPY target/spring-ocr-0.0.1-SNAPSHOT.jar /app/spring-ocr.jar

# Expose the port that Spring Boot will run on
EXPOSE 8081

# Run the Spring Boot application
ENTRYPOINT ["java", "-jar", "/app/spring-ocr.jar"]
