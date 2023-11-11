#!/bin/bash
timestamp=$(date +%Y%m%d%H%M%S)
git pull origin --ff-only
mvn clean install -Dmaven.test.skip=true
chmod 0777 ./target/retroframe.jar
docker image rm sr3u/retroframe:$timestamp
docker build -t sr3u/retroframe:$timestamp ./
docker push sr3u/retroframe:$timestamp
docker image rm sr3u/retroframe:$timestamp
