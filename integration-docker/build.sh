#!/bin/bash

set -e

DEV_VERSION=0.0.1
BASE=$(pwd)

cd $BASE/integration-docker
docker build -t thepandorasys/test:$DEV_VERSION .
