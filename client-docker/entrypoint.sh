#!/usr/bin/bash

set -e

if [ "$1" == "client" ]; then
    java -Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=8000 -Dspring.profiles.active=$PROFILE -DserverEndpoint=$SERVER_ENDPOINT -DtargetFolder=$TARGET_FOLDER -DjobDelay=$JOB_DELAY -Damazon.metadata=$AMAZON_METADATA  -jar /opt/client.jar
else
    exec "$@"
fi
