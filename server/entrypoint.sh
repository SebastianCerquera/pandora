#!/usr/bin/bash

if [ "$1" == "nginx" ]; then
    bash /opt/server.sh $N $BASE     
fi

exec $@
