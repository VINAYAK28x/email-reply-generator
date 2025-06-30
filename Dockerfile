# Stage 1: Build the Spring Boot application using Java 17
FROM eclipse-temurin:17-jdk AS build
WORKDIR /app
COPY . .

# Fix: make mvnw executable
RUN chmod +x mvnw

RUN ./mvnw clean package -DskipTests

# Stage 2: Run the Spring Boot app with Java 17 JRE
FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
