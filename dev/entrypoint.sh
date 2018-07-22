#!/bin/bash
set -x

if [ $1 == "bash" ]; then
    exec bash
elif [ $1 == "compile" ]; then
    cd /home/ubuntu/workspace
    mvn clean install
else
    exec "$@"
fi           
