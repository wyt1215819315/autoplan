FROM openjdk:8-jdk-alpine

VOLUME /tmp

ADD ./target/*.jar /auto_plan.jar

EXPOSE 26666

ENTRYPOINT ["java","-jar","auto_plan.jar","--spring.config.location=/tmp/yml/application.yml"]