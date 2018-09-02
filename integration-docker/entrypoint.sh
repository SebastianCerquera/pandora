#!/usr/bin/bash

set -e

echo ${USER}
bash /opt/adduser.sh ${USER} ${USER}

if [ "$1" == "test" ]; then
    mkdir /tmp/test && cd /tmp/test
    chown ${USER}:${USER} /tmp/test
    ## Lo hago para que el grupo de sudo se cargue a la sesion
    su -c "whoami" ${USER}
    su -c "bash /opt/integration/simple-test.sh client-jenkins" ${USER}
else
    exec "$@"
fi
