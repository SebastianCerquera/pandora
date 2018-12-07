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
echo "The client is registered, its id is: $CLIENT_ID"

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
dd if=/dev/zero of=1.jpg count=10 bs=1M 2>/dev/null
dd if=/dev/zero of=2.jpg count=10 bs=1M 2>/dev/null
dd if=/dev/zero of=3.jpg count=10 bs=1M 2>/dev/null
 
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

RESULT_CODE=$(curl -D - -XDELETE pandora:8080/v1/problems/$ID 2>/dev/null | perl -lne '/HTTP\/1\.\d+\s*(.+)/ && print $1')

if [ "x$DELETE_RESULT" ==  "x500" ]; then
    echo "Something went wrong, there shuld have been a problem"
    exit 100
fi

if [ "x$DELETE_RESULT" ==  "x200" ]; then
    echo "The problems was succesfully deleted, the client was already sinced"
fi

while [ "x$DELETE_RESULT" ==  "x202" ]; do
    RESULT_CODE=$(curl -D - -XDELETE pandora:8080/v1/problems/$ID 2>/dev/null | perl -lne '/HTTP\/1\.\d+\s*(.+)/ && print $1')
    echo "Deleting problem, waiting for clients to sync."
    sleep 60
done

if [ "x$DELETE_RESULT" ==  "x500" ]; then
    echo "The problems was succesfully deleted"
fi

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
