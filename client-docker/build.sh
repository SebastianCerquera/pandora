#!/usb/bin/bash

set -e

DEV_VERSION=$1
BASE=$(pwd)

cd $BASE/dev/
sudo docker build -t pandora/dev:$DEV_VERSION .

sudo docker run --rm -v $HOME/.m2/:/root/.m2/ -v $BASE/client:/root/workspace -it pandora/dev:$DEV_VERSION compile

cd $BASE/client
VERSION=$(cat pom.xml | perl -e '$s = 1; while(<>){print $_ if($s); $s=0 if(/parent/); $s=1 if(/\/parent/); }' | perl -ne '/<version>([\d\.]+)(RELEASE)?<\/version>/ && print "$1$2"')

cd $BASE/client-docker
cp $BASE/client/target/client-$VERSION.jar client.jar
sudo docker build -t pandora/client:$VERSION .
sudo docker tag pandora/client:$VERSION pandora/client:stable

