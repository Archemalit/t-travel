FROM eclipse-temurin:21-jdk-alpine

ARG APP_DIR=/opt/app
WORKDIR $APP_DIR

COPY build/libs/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "./app.jar"]