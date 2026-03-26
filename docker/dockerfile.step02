FROM eclipse-temurin:17-jdk-alpine AS builder
WORKDIR /app
COPY . .
COPY application.yml src/main/resources/application.yml
RUN chmod +x ./gradlew && ./gradlew build -x test

FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=builder /app/build/libs/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]