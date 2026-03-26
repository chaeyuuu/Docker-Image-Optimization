FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app
COPY . .
COPY application.yml src/main/resources/application.yml
RUN chmod +x ./gradlew && ./gradlew build -x test
EXPOSE 8080
CMD ["java", "-jar", "build/libs/docker-optimization-0.0.1-SNAPSHOT.jar"]