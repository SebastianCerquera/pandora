#!/usr/bin/bash

set -e

if [ "$1" == "client" ]; then
    java -DserverEndpoint=$SERVER_ENDPOINT -DtargetFolder=$TARGET_FOLDER -DjobDelay=$JOB_DELAY  -jar /opt/client.jar
else
    exec "$@"
fi
