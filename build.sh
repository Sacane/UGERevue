#!/bin/bash

dockerImageName="java_test_microservice:latest"
jarFilePath="RevevueApp/target/RevevueApp-0.0.1-SNAPSHOT.jar"
containerName="java_test_microservice"
port=7777

echo "Etape 1 : Maven Package"
mvn package

echo "Etape 2 : Création de l'image Docker"
if ! docker ps -a --format '{{.Image}}' | grep -q "$dockerImageName"; then
    docker build -t "$dockerImageName" .
else
    echo "L'image Docker existe déjà."
fi

echo "Etape 3 : Lancement de l'image Docker"
if docker ps -a --filter "name=$containerName" --format '{{.Names}}' | grep -q "$containerName"; then
    echo "Le conteneur $containerName existe déjà, démarrage en cours..."
    docker start "$containerName"
else
    docker run -d -p $port:7777 --name "$containerName" "$dockerImageName"
fi



echo "Etape 4 : Lancement du JAR"
sleep 5; java -jar "$jarFilePath" --spring.profiles.active=prod

echo "Toutes les étapes ont été exécutées avec succès."