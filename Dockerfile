FROM java:8

VOLUME /tmp

ADD target/*.jar /auto_plan.jar

EXPOSE 26666

ENTRYPOINT ["java","-jar","auto_plan.jar"]