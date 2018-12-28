#!/usr/bin/bash

set -e

HOSTNAME_FILE=/opt/hostname

ARGS=""
if [ -f "$HOSTNAME_FILE" ]; then
    ARGS=$(cat $HOSTNAME_FILE | perl -ne 'print $_ if($. == 1)')
fi

if [ "$1" == "metadata" ]; then
    exec python /opt/amazon.py $ARGS
else
    exec "$@"
fi
