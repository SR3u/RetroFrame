#!/bin/bash
git pull origin --ff-only
mvn clean install -Dmaven.test.skip=true
chmod 0777 ./target/retroframe.jar
docker image rm sr3u/retroframe:latest
docker build -t sr3u/retroframe:latest ./
