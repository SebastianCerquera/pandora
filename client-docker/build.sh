#!/usr/bin/bash

set -e

DEV_VERSION=$1
BASE=$(pwd)
SOURCE_PATH=client

cd $BASE/dev/development
docker build -t pandora/dev:$DEV_VERSION .

SCM=$(basename $BASE)
chmod 777 $BASE/$SOURCE_PATH
docker run --rm -v mvn-cache:/home/pandora/.m2/ -v jenkins-source:/home/pandora/workspace -t pandora/dev:$DEV_VERSION compile $SCM/$SOURCE_PATH

cd $BASE/$SOURCE_PATH
VERSION=$(cat pom.xml | perl -e '$s = 1; while(<>){print $_ if($s); $s=0 if(/parent/); $s=1 if(/\/parent/); }' | perl -ne '/<version>([\d\.]+)(RELEASE)?<\/version>/ && print "$1$2"')

cd $BASE/client-docker
cp $BASE/$SOURCE_PATH/target/client-$VERSION.jar client.jar
docker build -t pandora/client:$VERSION .
docker tag pandora/client:$VERSION pandora/client:stable

