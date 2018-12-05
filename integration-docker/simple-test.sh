#!/bin/bash

set -e

CLIENT_DOCKER=$1

###
# Checks that the client properly registered.
###
CLIENTS=$(curl pandora:8080/v1/clients 2>/dev/null)
CLIENTS_COUNT=$(echo $CLIENTS | jq length )

if [ -z "$CLIENTS_COUNT" -o  $CLIENTS_COUNT -ne  1 ]; then
    echo "Something went wrong, the client failed to register"
    exit 100
fi

CLIENT_ID=$(echo $CLIENTS | jq '.[] | .id')

###
#   CREATES PROBLEMS
### 
curl pandora:8080/v1/problems
 
RAW=$(curl -XPOST pandora:8080/v1/problems/150 2>/dev/null)
ID=$(echo "$RAW" | perl -ne '/\"id\":\s*(\d+)/ && print $1')
 
KEY=$(curl pandora:8080/v1/problems/$ID 2>/dev/null)
perl -le '
use bigint;

$str = do { local $/; <STDIN> };

$m = $1 if($str =~ /"modulus":"(\d+)"/);
$s = $1 if($str =~ /"secret":"(\d+)"/);

$r=$m/$s;
if($r*$s == $m){
    print "You got it bro: " . $r*$s ." == $m";
} else {
  exit(1)
}
' <<EOF
$KEY
EOF
 
touch 1.jpg 2.jpg 3.jpg
dd if=/dev/zero of=1.jpg count=10 bs=1M
dd if=/dev/zero of=2.jpg count=10 bs=1M
dd if=/dev/zero of=3.jpg count=10 bs=1M
 
for i in 1.jpg	2.jpg  3.jpg; do 
    curl -XPOST -F "file=@$i" -F "fileName=$i"  pandora:8080/v1/problems/$ID/images;
    echo $?
done
 
echo "This one is yours: $ID"
curl pandora:8080/v1/problems

COMPLETED=$(echo $RAW | perl -ne 's/"state":"CREATED"/"state":"COMPLETED"/g; print $_')
curl -XPUT -H 'content-type: application/json' pandora:8080/v1/problems/$ID -d $COMPLETED

curl pandora:8080/v1/problems/$ID

[ -d output ] || mkdir output
curl pandora:8080/v1/problems/$ID/images -o output/safe.tar 2>/dev/null
 
cd output
tar xf safe.tar 2>/dev/null
PAYLOAD_ORIGINAL=$(ls | sort | perl -le '$r = ""; while(<>){if(/.*\.jpg/){chomp(); $r=$r.$_;}} print $r;')
if [ "x$PAYLOAD_ORIGINAL" == "x1.jpg2.jpg3.jpg" ]; then
    echo "The server generated the proper payload: $PAYLOAD_ORIGINAL"
else
    exit 100
fi
cd ..
 
sleep 300

curl -XDELETE pandora:8080/v1/problems/$ID

[ -d payload ] || mkdir payload
docker cp $CLIENT_DOCKER:/tmp/runs/$ID/safe.tar payload/safe.tar

cd payload
tar xf safe.tar 2>/dev/null
PAYLOAD_ENCRYPTED=$(ls | sort | perl -le '$r = ""; while(<>){if(/.*\.jpg/){chomp(); $r=$r.$_;}} print $r;')
if [ "x$PAYLOAD_ENCRYPTED" == "x1.jpg2.jpg3.jpg" ]; then
    echo "The client produced the proper payload: $PAYLOAD_ENCRYPTED"
else
    exit 100
fi
cd ..
