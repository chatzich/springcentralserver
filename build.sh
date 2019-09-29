#!/usr/bin/sh

rm -rf target/
mvn package
docker-compose up --build 

