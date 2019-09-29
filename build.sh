#!/usr/bin/sh

rm -rf target/
mvn package

sudo docker rm sort-rest
sudo docker rmi sort-rest
sudo docker build -t sort-rest .

sudo docker rmi sort-db
sudo docker run -d --name sort-db -e POSTGRES_USER=postgres -e POSTGRES_PASSWORD=postgres -e POSTGRES_DB=postgres postgres:9.4.5
sudo docker run -d --name sort-rest --link sort-db:sort-db sort-rest

