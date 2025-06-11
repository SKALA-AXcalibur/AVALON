FROM docker.io/library/eclipse-temurin:21-jdk-alpine AS build
WORKDIR /app
COPY . .
RUN ./gradlew bootJar --no-daemon

FROM docker.io/library/eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /app/build/libs/*.jar app.jar
COPY --from=build /app/config /app/config
EXPOSE 8080 8081
ENTRYPOINT ["java", "-jar", "app.jar", "--spring.profiles.active=prod"]
