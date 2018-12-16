#!/bin/bash

set -e

## This is not the name of the client container but the suffix.
CLIENT_DOCKER=$1

BASE=$(pwd)

docker run -d --name server-proxy --link server-jenkins:pandora ssig33/simple-reverse-proxy pandora:8080

docker run -d --name client-jenkins-3 -e JOB_DELAY=60 -e PROFILE=default -e SERVER_ENDPOINT=http://pandora:80 -e TARGET_FOLDER=/tmp/runs -e AMAZON_METADATA=http://dummy:5200/public-hostname --link server-proxy:pandora --link metadata-dummy:dummy -t pandora/client:stable client

sleep 120

###
# Checks that the client properly registered.
###
CLIENTS=$(curl pandora:8080/v1/clients 2>/dev/null)
CLIENTS_COUNT=$(echo $CLIENTS | jq length )

if [ -z "$CLIENTS_COUNT" -o  $CLIENTS_COUNT -ne 1 ]; then
    echo "Something went wrong, the client failed to register"
    exit 100
fi


CLIENT_ID_1=$(echo $CLIENTS | jq '.[0].id')
echo "The client is registered, its id is: $CLIENT_ID_1"

###
#  Turns down the proxy, checks the client was marked as down.
###

docker stop server-proxy

sleep 120

CLIENTS=$(curl pandora:8080/v1/clients 2>/dev/null)
CLIENTS_COUNT=$(echo $CLIENTS | jq length )

if [ -z "$CLIENTS_COUNT" -o  $CLIENTS_COUNT -ne  0 ]; then
    echo "Something went wrong, the client was not marked as down"
    exit 100
else
    echo "SUCCESS the client was properly marked as down"
fi


###
#  Turns on the proxy, checks the client is back.
###


docker start server-proxy

sleep 120

CLIENTS=$(curl pandora:8080/v1/clients 2>/dev/null)
CLIENTS_COUNT=$(echo $CLIENTS | jq length )

if [ -z "$CLIENTS_COUNT" -o  $CLIENTS_COUNT -ne  1 ]; then
    echo "Something went wrong, the client should be back"
    exit 100
else
    echo "SUCCESS the client was marked as active."
fi


docker stop server-proxy

docker stop client-jenkins-3
