FROM eclipse-temurin:17-jre-jammy

WORKDIR /app

ARG JAR_FILE=target/almond-back-start-1.0.0-SNAPSHOT.jar
COPY ${JAR_FILE} app.jar

ENV JAVA_OPTS=""
ENV SPRING_PROFILES_ACTIVE=dev

EXPOSE 8082

ENTRYPOINT ["sh", "-c", "java ${JAVA_OPTS} -jar app.jar"]
