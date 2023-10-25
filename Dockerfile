FROM openjdk:17

ARG JAR_FILE="./build/libs/p2k-0.0.1-SNAPSHOT.jar"

COPY ${JAR_FILE} p2k.jar

ENV PROFILE prod,aws,mariaDB

ENTRYPOINT ["java","-Dspring.profiles.active=${PROFILE}","-jar","/p2k.jar"]