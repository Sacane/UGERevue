## Contributors 

- Ramaroson Rakotomihamina Johan ```johan.ramaroson@gmail.com```
- Gaudet Clément ```derkeethus96@gmail.com```
- Tellier Quentin ```quentintellier01@yahoo.com```
- Menaa Mathis ```mathis.menaa2@gmail.com```
- Regueme Yohann ```reguemeyohannsup@gmail.com```

## How to build

- To download dependencies use the following command : 
  - On linux install maven then ```mvn clean install -U```
  - On windows use the given maven script ```.\mvnw.cmd clean install```
- To package for production :
  - ```mvn package```
The front will be generated and move into the classpath/static folder

- To run the application : ```java -jar UGERevevue-xxx.jar```
    - app root is set to port 8080 by default

### In dev mode 

In dev mode you should run the back and front separately to be able to see the front renderer on-change

- Go to the ```src/main/revevue-front``` folder then use ```ng serve``` to run the front
- Launch the back in your preferred IDE or on eclipse.
