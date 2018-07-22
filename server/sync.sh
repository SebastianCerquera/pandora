#!/bin/bash

set -e

VERSION=pandora/server:0.0.5

if [ "$#" -lt 2 ]; then
   echo "It requieres both the endpoint and the pem file"
   exit 1
fi

ID=$1

HOST=$2
PORT=22
USER=pandora

PEM=$3

BASE=/home/pandora
IMAGES=$BASE/runs
SOURCE=$BASE/pandora

ssh -p $PORT -i $PEM $USER@$HOST 'bash -s' <<EOF
cd $SOURCE/server
git pull origin master
sudo docker build -t $VERSION .
EOF

rsync -a --progress -e "ssh -p $PORT -i $PEM" $(pwd)/$ID/ $USER@$HOST:$IMAGES/$ID/ > /dev/null
