#!/bin/bash
set -x

if [ $1 == "bash" ]; then
    exec bash
elif [ $1 == "compile" ]; then
    cd /home/pandora/workspace

    SCM=$2

    if [ ! -d "$SCM" ]; then
        echo "The SCM directory does not exists"
        exit 1
    fi

    cd $SCM    
    mvn clean install
else
    exec "$@"
fi           
