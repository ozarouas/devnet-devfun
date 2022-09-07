From adoptopenjdk/openjdk11:alpine-jre
RUN mkdir /opt/app
COPY target/backendsecurity-0.0.1-SNAPSHOT.jar /opt/app
EXPOSE 8080
ENTRYPOINT ["java","-jar","/opt/app/backendsecurity-0.0.1-SNAPSHOT.jar"]