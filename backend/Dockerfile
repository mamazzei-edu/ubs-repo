FROM eclipse-temurin:21-jdk-alpine
RUN apk update && apk upgrade --no-cache
VOLUME /tmp
WORKDIR /app
COPY . .
RUN ./mvnw clean install -DskipTests
#ARG JAR_FILE=target/*.jar
# COPY ${JAR_FILE} backend-0.0.1-SNAPSHOT.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","target/backend-0.0.1-SNAPSHOT.jar"]