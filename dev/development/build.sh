#!/usb/bin/bash

set -e

DEV_VERSION=$1
BASE=$(pwd)
SOURCE_PATH=dev/development

cd $BASE/$SOURCE_PATH
docker build -t pandora/dev:$DEV_VERSION .
