FROM eclipse-temurin:21-jre-alpine
RUN apk update && apk upgrade --no-cache
VOLUME /tmp
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} backend-0.0.1-SNAPSHOT.jar
ENTRYPOINT ["java","-jar","/backend-0.0.1-SNAPSHOT.jar"]