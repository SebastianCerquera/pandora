#!/usr/bin/bash

set -e

BASE=$(pwd)

docker tag pandora/metadata-dummy:0.0.1 thepandorasys/metadata-dummy:0.0.1

CREDENTIALS=/tmp/credentials/secret

if [ -f  "$CREDENTIALS" ]; then
    REGISTRY_USER=$(cat $CREDENTIALS | perl -ne 'print $_ if($. == 1)')
    REGISTRY_PASSWORD=$(cat $CREDENTIALS | perl -ne 'print $_ if($. == 2)')
    echo $REGISTRY_PASSWORD | docker login -u $REGISTRY_USER --password-stdin
    docker push thepandorasys/metadata-dummy:0.0.1
else
    echo "It won't push the images because there is no credentials folder"
fi

