#!/usr/bin/bash

set -e

if [ "$1" == "server" ]; then
    java -Drsagen=$RSAGEN  -jar /opt/server.jar
else
    exec "$@"
fi
