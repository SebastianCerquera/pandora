#!/usr/bin/bash

set -e

echo ${USER}
bash /opt/adduser.sh ${USER} ${USER}

if [ "$1" == "test" ]; then
    mkdir /tmp/test && cd /tmp/test
    bash /opt/integration/simple-test.sh client-jenkins
else
    exec "$@"
fi
