#!/bin/bash
set -x

if [ $1 == "bash" ]; then
    exec bash
elif [ $1 == "compile" ]; then
    echo ${USER}
    bash /opt/adduser.sh ${USER} ${USER}
    
    cd /home/pandora/workspace

    SCM=$2

    if [ ! -d "$SCM" ]; then
        echo "The SCM directory does not exists"
        exit 1
    fi

    cd $SCM
    su -c "mvn clean install" ${USER}

    chmod 777 -R .
else
    exec "$@"
fi           
