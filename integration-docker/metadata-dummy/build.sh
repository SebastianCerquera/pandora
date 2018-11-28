#!/bin/bash

set -e

DEV_VERSION=$1
BASE=$(pwd)

cd $BASE/integration-docker/metadata-dummy/

docker build -t pandora/metadata-dummy:$DEV_VERSION .
