FROM eclipse-temurin:17-jdk
WORKDIR /app
COPY . .
COPY application.yml src/main/resources/application.yml
RUN chmod +x ./gradlew && ./gradlew build -x test
EXPOSE 8080
CMD ["java", "-jar", "app.jar"]