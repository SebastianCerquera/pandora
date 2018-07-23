#!/usr/bin/bash

set -e

if [ "$1" == "client" ]; then
    java -DserverEndpoint=$SERVER_ENDPOINT -DtargetFolder=$TARGET_FOLDER  -jar /opt/client.jar
else
    exec "$@"
fi
