#!/bin/bash

set -e

mkdir /tmp/test && cd /tmp/test

CLIENT_DOCKER=$1

curl pandora:8080/v1/problems
 
RAW=$(curl -XPOST pandora:8080/v1/problems/150 2>/dev/null)
ID=$(echo "$RAW" | perl -ne '/\"id\":\s*(\d+)/ && print $1')
 
KEY=$(curl pandora:8080/v1/problems/$ID 2>/dev/null)
perl -le '
use bigint;

$str = do { local $/; <STDIN> };

$m = $1 if($str =~ /"modulus":"(\d+)"/);
$s = $1 if($str =~ /"secret":"(\d+)"/);

print $s;
print $m;

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
echo "1234567890" > 1.jpg
echo "1234567890" > 2.jpg
echo "1234567890" > 3.jpg
 
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
fi
cd ..
