FROM openjdk:21

ARG JAR_NAME=JavaTestsMicroservice.jar

COPY RevevueTestService/target/$JAR_NAME /microservices/$JAR_NAME
COPY RevevueTestService/lib/junit-jupiter-5.10.1.jar /microservices/
COPY RevevueTestService/lib/junit-jupiter-api-5.10.1.jar /microservices/
COPY RevevueTestService/lib/junit-jupiter-engine-5.10.1.jar /microservices/
COPY RevevueTestService/lib/junit-jupiter-params-5.10.1.jar /microservices/
COPY RevevueTestService/lib/apiguardian-api-1.1.2.jar /microservices/
COPY RevevueTestService/lib/opentest4j-1.3.0.jar /microservices/
COPY RevevueTestService/lib/junit-platform-launcher-1.10.2.jar microservices/
COPY RevevueTestService/lib/junit-platform-commons-1.10.2.jar microservices/
COPY RevevueTestService/src/test/resources/FakeJavaFiles/HelloWorld.java microservices/
COPY RevevueTestService/src/test/resources/FakeJavaFiles/HelloWorldTest.java microservices/



WORKDIR /microservices

ENV WORK_ENV=PROD

ENTRYPOINT ["java", "-jar", "JavaTestsMicroservice.jar", "-Dfile.encoding=UTF-8", "-Dsun.stdout.encoding=UTF-8", "-Dsun.stderr.encoding=UTF-8", "-Dspring.application.admin.enabled=true"]
