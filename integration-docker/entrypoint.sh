#!/usr/bin/bash

set -e

if [ "$1" == "test" ]; then
    mkdir /tmp/test && cd /tmp/test
    bash /opt/simple-test.sh client-jenkins
else
    exec "$@"
fi
