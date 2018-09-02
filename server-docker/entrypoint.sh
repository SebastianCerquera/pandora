#!/usr/bin/bash

set -e

if [ "$1" == "server" ]; then
    java -Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=8000 -Drsagen=$RSAGEN  -jar /opt/server.jar
else
    exec "$@"
fi
