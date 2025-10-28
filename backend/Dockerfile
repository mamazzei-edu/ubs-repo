FROM eclipse-temurin:21-jdk-alpine
RUN apk update && apk upgrade --no-cache
VOLUME /tmp
WORKDIR /app
COPY . .
RUN ./mvnw clean install -DskipTests
EXPOSE 8080
ENTRYPOINT ["java","-jar","target/backend-0.0.1-SNAPSHOT.jar"]