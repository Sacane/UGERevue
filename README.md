# UGE Revevue

## Contributors 

- Ramaroson Rakotomihamina Johan (Tech lead) ```johan.ramaroson@gmail.com```
- Menaa Mathis ```mathis.menaa2@gmail.com```
- Tellier Quentin ```quentintellier01@yahoo.com```
- Gaudet Clément  ```derkeethus96@gmail.com```
- Regueme Yohann ```reguemeyohannsup@gmail.com```

## Requires

To make sure you can build the entire project in a comfortable way, you must install the following dependencies :

- **Java** (JDK 21 or later, mandatory)
- **Docker** (mandatory)
- **npm**, mandatory for development environment, optional for production
- **Maven** (optional)

IntelliJ Idea (optional) is highly recommended due to its many tools for build and setup

## How to build

For all this section, the ```mvn``` command line instruction can be replace by the given maven script for windows : 
```.\mvnw.cmd```

- To download dependencies use the following command ```mvn clean install -U```
- This project is divide in 2 sub-modules, one for the web application, the other for a micro services used by the main application. The following command line will generate the jars for each modules : ```mvn package```
- After packaging your jars you must run the dockerFile into your environment with this command ```docker build -t <imageName> .```
- Then run the following docker cmd to deploy in a container ```docker run -p 7777:7777 --add-host=<your_host> <imageName>``` by replacing "imageName" and "<your_host>" variables

**We highly recommend you to use Docker even in a development environment, for security and reduce the margin of errors after deployment**

## How to run

### In dev mode

In dev mode you can either launch the packaged jar in your local environment or in your favorite IDEA (or eclipse) by loading a maven project.

To run the front you can go to the ```RevevueApp/src/main/revevue-front/package.json``` file and run the ```ng serve``` command. This will launch the front server listening into the 4200 port.


### For deployment

To deploy you must follow the *build section*, specially for the docker-part.

***DO NOT*** deploy the microservice without using a container, with it you servers are prevent for any future security issues using coding injection.