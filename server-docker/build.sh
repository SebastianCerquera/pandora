#!/usb/bin/bash

set -e

DEV_VERSION=$1
BASE=$(pwd)
SOURCE_PATH=server/complete

SCM=$(basename $BASE)
chmod -R jenkins:jenkins $BASE/$SOURCE_PATH
docker run --rm -v mvn-cache:/home/pandora/.m2/ -v jenkins-source:/home/pandora/workspace -t pandora/dev:$DEV_VERSION compile $SCM/$SOURCE_PATH

cd $BASE/$SOURCE_PATH
VERSION=$(cat pom.xml | perl -e '$s = 1; while(<>){print $_ if($s); $s=0 if(/<parent>/);}' | perl -ne '/<version>([\d\.]+)(RELEASE)?<\/version>/ && print "$1$2"')

cd $BASE/server-docker
cp $BASE/$SOURCE_PATH/target/server-$VERSION.jar server.jar
docker build -t pandora/server:$VERSION .
docker tag pandora/server:$VERSION pandora/server:stable

