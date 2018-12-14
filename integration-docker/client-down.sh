#!/bin/bash

set -e

## This is not the name of the client container but the suffix.
CLIENT_DOCKER=$1

BASE=$(pwd)

###
# Checks that the client properly registered.
###
CLIENTS=$(curl pandora:8080/v1/clients 2>/dev/null)
CLIENTS_COUNT=$(echo $CLIENTS | jq length )

if [ -z "$CLIENTS_COUNT" -o  $CLIENTS_COUNT -ne  2 ]; then
    echo "Something went wrong, the client failed to register"
    exit 100
fi


CLIENT_ID_1=$(echo $CLIENTS | jq '.[0].id')
echo "The client is registered, its id is: $CLIENT_ID_1"

CLIENT_ID_2=$(echo $CLIENTS | jq '.[1].id')
echo "The client is registered, its id is: $CLIENT_ID_2"


###
#  Turns down a client and waits that it is properly unregistered.
###

docker stop "$CLIENT_DOCKER-1"

sleep 120

CLIENTS=$(curl pandora:8080/v1/clients 2>/dev/null)
CLIENTS_COUNT=$(echo $CLIENTS | jq length )

if [ -z "$CLIENTS_COUNT" -o  $CLIENTS_COUNT -ne  1 ]; then
    echo "Something went wrong, the client was not marked as down"
    exit 100
fi
