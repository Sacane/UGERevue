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
- Then run the following docker cmd to deploy in a container ```docker run -p 7777:7777 <imageName>``` by replacing "imageName" and "<your_host>" variables

**We highly recommend you to use Docker even in a development environment, for security and reduce the margin of errors after deployment**

## How to deploy

### In dev mode

In dev mode you can either launch the packaged jar in your local environment or in your favorite IDEA (or eclipse) by loading a maven project.

To run the front you can go to the ```RevevueApp/src/main/revevue-front/package.json``` file and run the ```ng serve``` command. This will launch the front server listening into the 4200 port.


### For deployment

To deploy you must follow the *build section*, specially for the docker-part.

After packaging the jars and make sure that the docker container runs your micro service, you can run the following command : 
```sh
java -jar /path/to/your/RevevueApp-0.0.1-SNAPSHOT.jar --spring.active.profiles=prod
```

*Beware the --spring.active.profiles is necessary to specify that you are running your application in production mode.*

***DO NOT*** deploy the microservice without using a docker container, with it you servers are prevent for any future security issues using coding injection.

### __If you want, there is a script (build.sh) which build the projet, create the docket image, launch a container and run the application.__

In localhost, your server run now on the following link : http://localhost:8080/ and http://localhost:8080/light/ for the light version of the application.

## Environment variables

- DATASOURCE_URL - Data Source Url of the database used, H2 by default.
- DATASOURCE_DRIVER - Driver of the database used, H2 driver by default.
- USERNAME_DB - Username of the database.
- PASSWORD_DB - Password of the database.
- CONSOLE_H2 - In the case you're using H2, boolean to set the console, false by default.

**Warn** If you want to run the tests you must stop the docker container (and free the 7777 port ip)