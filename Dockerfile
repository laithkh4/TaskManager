FROM openjdk:17.0.1
ADD target/TaskManager.jar TaskManager.jar
EXPOSE 8085
ENTRYPOINT ["java","-jar","/TaskManager.jar"]