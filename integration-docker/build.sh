#!/bin/bash

set -e

DEV_VERSION=$1
BASE=$(pwd)

cd $BASE/integration-docker
docker build -t pandora/test:$DEV_VERSION .
