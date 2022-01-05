FROM java:8
#RUN sed -i "s/archive.ubuntu./mirrors.aliyun./g" /etc/apt/sources.list
#RUN sed -i "s/deb.debian.org/mirrors.aliyun.com/g" /etc/apt/sources.list
#RUN sed -i "s/security.debian.org/mirrors.aliyun.com\/debian-security/g" /etc/apt/sources.list
#RUN sed -i "s/httpredir.debian.org/mirrors.aliyun.com\/debian-security/g" /etc/apt/sources.list
VOLUME /tmp
ADD target/auto_plan.jar /auto_plan.jar
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/auto_plan.jar"]