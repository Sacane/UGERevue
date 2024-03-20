FROM openjdk:21

EXPOSE $EXPOSE_PORT 7777
ARG JAR_NAME=JavaTestsMicroservice.jar

COPY RevevueTestService/target/$JAR_NAME /microservices/$JAR_NAME

WORKDIR /microservices

ENTRYPOINT ["java", "-jar", "JavaTestsMicroservice.jar"]