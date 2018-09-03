#!/bin/bash

set -e

DEV_VERSION=$1
BASE=$(pwd)

cd $BASE/integration-docker

perl -pi -e 's/#VERSION#/'"$DEV_VERSION"'/g' Dockerfile
docker build -t pandora/test:$DEV_VERSION .
