#!/usr/bin/bash

set -e

if [ "$1" == "test" ]; then
    bash /opt/simple-test.sh client-jenkins
else
    exec "$@"
fi
