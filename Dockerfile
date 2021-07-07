#FROM java:8
#
## Install maven
#RUN apt-get -y update && apt-get install -y maven
#
#WORKDIR /code
#
## Prepare by downloading dependencies
#ADD demo-0.0.1-SNAPSHOT.jar  /app/docker-test.jar
#
## Adding source, compile and package into a fat jar
#ADD src /code/src
#RUN ["mvn", "package"]
#
#EXPOSE 8080
#CMD ["java", "-jar", "/app/dpcker-test.jar"]

FROM java:8
ADD demo-0.0.1-SNAPSHOT.jar  /app/docker-test.jar
EXPOSE 8888
ENTRYPOINT ["java","-jar","/app/docker-test.jar"]
