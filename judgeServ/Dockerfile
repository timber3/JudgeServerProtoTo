FROM openjdk:17-jdk-alpine

WORKDIR /app

ARG JAR_FILE="target/judgeServ-0.0.1-SNAPSHOT.jar"

COPY ${JAR_FILE} test.jar

ENTRYPOINT ["java", "-jar", "test.jar"]