FROM openjdk:11
MAINTAINER sam
COPY target/factory-0.0.1-SNAPSHOT.jar /opt/factory.jar
ENTRYPOINT ["java","-jar","/opt/factory.jar"]