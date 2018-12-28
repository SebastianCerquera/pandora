#!/usr/bin/bash

set -e

if [ "$1" == "server" ]; then
    java -Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=8000 -Drsagen=$RSAGEN -DclientTimeout=$CLIENT_TIMEOUT  -jar /opt/server.jar
else
    exec "$@"
fi
