#!/bin/bash
git pull origin --ff-only
mvn clean install -Dmaven.test.skip=true
docker image rm sr3u/retroframe:latest
docker build -t sr3u/retroframe:latest ./
