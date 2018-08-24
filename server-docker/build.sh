#!/usb/bin/bash

set -e

DEV_VERSION=$1
BASE=$(pwd)

cd $BASE/dev/
sudo docker build -t pandora/dev:$DEV_VERSION .

sudo docker run --rm -v $HOME/.m2/:/root/.m2/ -v $BASE/server/complete:/root/workspace -it pandora/dev:$DEV_VERSION compile

cd $BASE/server/complete
VERSION=$(cat pom.xml | perl -e '$s = 1; while(<>){print $_ if($s); $s=0 if(/<parent>/);}' | perl -ne '/<version>([\d\.]+)(RELEASE)?<\/version>/ && print "$1$2"')

cd $BASE/server-docker
cp $BASE/server/complete/target/server-$VERSION.jar server.jar
sudo docker build -t pandora/server:$VERSION .
sudo docker tag pandora/server:$VERSION pandora/server:stable

