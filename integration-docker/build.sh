#!/bin/bash

set -e

DEV_VERSION=0.0.1
BASE=$(pwd)

sudo bash $BASE/client-docker/build.sh $DEV_VERSION
sudo bash $BASE/server-docker/build.sh $DEV_VERSION

MYDATE=$(date  "+%d%m%y--%H%M")
sudo docker run -d --name server-$MYDATE -p 8080:8080 -e RSAGEN=/opt/rsagen.sh -t pandora/server:stable server

sleep 10
sudo docker run -d --name client-$MYDATE -e JOB_DELAY=60 -e SERVER_ENDPOINT=http://pandora:8080 -e TARGET_FOLDER=/tmp/runs --link server-$MYDATE:pandora -t pandora/client:stable client

sleep 120

bash $BASE/integration-docker/simple-test.sh $MYDATE

sudo docker stop server-$MYDATE
sudo docker stop client-$MYDATE

sudo docker rm server-$MYDATE
sudo docker rm client-$MYDATE

