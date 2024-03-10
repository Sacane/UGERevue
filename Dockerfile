FROM openjdk:21

ARG JAR_NAME=JavaTestsMicroservice.jar

COPY RevevueTestService/target/$JAR_NAME /microservices/$JAR_NAME
COPY RevevueTestService/lib/junit-jupiter-5.10.1.jar /microservices/
COPY RevevueTestService/lib/junit-jupiter-api-5.10.2.jar /microservices/
COPY RevevueTestService/lib/junit-jupiter-engine-5.10.1.jar /microservices/
COPY RevevueTestService/lib/junit-jupiter-params-5.10.1.jar /microservices/
COPY RevevueTestService/lib/apiguardian-api-1.1.2.jar /microservices/
WORKDIR /microservices

ENV WORK_ENV=PROD

ENTRYPOINT ["java", "-jar", "JavaTestsMicroservice.jar"]