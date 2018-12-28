#!/usr/bin/bash

set -e

if [ "$1" == "metadata" ]; then
    exec python /opt/amazon.py
else
    exec "$@"
fi
