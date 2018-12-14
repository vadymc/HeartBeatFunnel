FROM openjdk:11-jdk-slim
MAINTAINER vadim.chekry@gmail.com
VOLUME /tmp
COPY build/libs/HeartBeatFunnel*.jar app.jar
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/app.jar"]
