FROM openjdk:17-jdk-alpine

WORKDIR /usr/src/app

COPY target/waste*.jar app.jar

EXPOSE 10883

ENTRYPOINT ["java","-jar","app.jar"]