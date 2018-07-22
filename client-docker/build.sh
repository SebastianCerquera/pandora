#!/usb/bin/bash

set -e

DEV_VERSION=$1
BASE=$(pwd)

cd $BASE/dev/
sudo docker build -t pandora/dev:$DEV_VERSION .

sudo docker run --rm -v $HOME/.m2/:/home/ubuntu/.m2/ -v $BASE/pandora/client:/home/ubuntu/workspace -it pandora/dev:$DEV_VERSION compile

cd $BASE/client
VERSION=$(cat pom.xml | perl -ne '/<version>([\d\.]+)<\/version>/ && print $1')

cd $BASE/client-docker
cp $BASE/client/target/client-$VERSION.jar client.jar
sudo docker build -t pandora/client:$VERSION .
sudo docker tag pandora/client:$VERSION pandora/client:stable

