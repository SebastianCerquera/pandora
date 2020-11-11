#!/bin/bash

PORT=48080
BASE=/home/pandora/
SRC=$BASE/runs

cd $SRC

DELAY=$(python -c 'a = 1800; print a')
curl p.qanomads.com:$PORT/v1/problems
 
RAW=$(curl -XPOST p.qanomads.com:$PORT/v1/problems/$DELAY 2>/dev/null)
ID=$(echo "$RAW" | perl -ne '/\"id\":\s*(\d+)/ && print $1' 2>/dev/null)
 
KEY=$(curl p.qanomads.com:$PORT/v1/problems/$ID 2>/dev/null)
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
 
for i in $(ls | grep jpg); do 
    curl -XPOST -F "file=@$i" -F "fileName=$i"  p.qanomads.com:$PORT/v1/problems/$ID/images;
    echo $?
done
 
echo "This one is yours: $ID"
curl p.qanomads.com:$PORT/v1/problems

COMPLETED=$(echo $RAW | perl -ne 's/"state":"CREATED"/"state":"READY"/g; print $_' 2>/dev/null)
curl -XPUT -H 'content-type: application/json' p.qanomads.com:$PORT/v1/problems/$ID -d $COMPLETED

curl p.qanomads.com:$PORT/v1/problems/$ID

## it is just to create a blank line
echo ""

[ -d output ] || mkdir output
curl p.qanomads.com:$PORT/v1/problems/$ID/images -o output/safe.tar 2>/dev/null

## The regular expression is duplicated below
RESULT_CODE=$(curl -D - -XDELETE p.qanomads.com:$PORT/v1/problems/$ID 2>/dev/null | perl -ne '/HTTP\/1\.\d+\s*(\d+)/ && print $1' 2>/dev/null)

if [ "x$RESULT_CODE" ==  "x500" ]; then
    echo "Something went wrong, there shuld have been a problem"
    exit 100
fi

if [ "x$RESULT_CODE" ==  "x200" ]; then
    echo "The problems was succesfully deleted, the client was already sinced"
fi

## There is a cocurrency error, if the runner keeps trying to delete the problem the clients might not sync properlt.
while [ "x$RESULT_CODE" ==  "x202" ]; do
    RESULT_CODE=$(curl -D - -XDELETE p.qanomads.com:$PORT/v1/problems/$ID 2>/dev/null | perl -ne '/HTTP\/1\.\d+\s*(\d+)/ && print $1' 2>/dev/null)
    echo "Deleting problem, waiting for clients to sync."
    sleep 60
done
 
if [ "x$RESULT_CODE" ==	 "x200" ]; then
    echo "The problem: $ID was succesfully deleted"
    curl p.qanomads.com:$PORT/v1/problems
fi


cd output
tar xf safe.tar

for i in $(ls | grep jpg); do 
    diff ../$i $i
    echo $?
done

 
cd $BASE

sudo umount $SRC
sudo dd if=/dev/zero of=$BASE/FS bs=1M count=1024
sudo /usr/sbin/mkfs.ext2 $BASE/FS
sudo mount $BASE/FS $SRC
sudo chmod 777 -R $SRC

exit
