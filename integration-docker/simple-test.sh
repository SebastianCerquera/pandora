#!/bin/bash

CLIENT_DOCKER=client-$1

curl localhost:8080/v1/problems
 
RAW=$(curl -XPOST localhost:8080/v1/problems/150 2>/dev/null)
ID=$(echo "$RAW" | perl -ne '/\"id\": \"(\d+)\"/ && print $1')
 
KEY=$(curl localhost:8080/v1/problems/$ID 2>/dev/null)
perl -e '
use bigint;
 
@A = <>;
$r=$A[1]/$A[2];
if($r*$A[2] == $A[1]){
    print "You got it bro: ".$A[2]*$r ." == $A[1]";
}
' <<EOF
$KEY
EOF
 
touch 1.jpg 2.jpg 3.jpg
echo "1234567890" > 1.jpg
echo "1234567890" > 2.jpg
echo "1234567890" > 3.jpg
 
for i in 1.jpg	2.jpg  3.jpg; do 
	curl -XPOST -F "file=@$i" -F "fileName=$i"  localhost:8080/v1/problems/$ID/images 2&> /dev/null;
done
 
echo "This one is yours: $ID"
curl localhost:8080/v1/problems
 
[ -d output ] || mkdir output
curl localhost:8080/v1/problems/$ID/images -o output/safe.tar 2>/dev/null
 
cd output
tar xf safe.tar 2>/dev/null
PAYLOAD_ORIGINAL=$(ls | sort | perl -le '$r = ""; while(<>){if(/.*\.jpg/){chomp(); $r=$r.$_;}} print $r;')
if [ "x$PAYLOAD_ORIGINAL" == "x1.jpg2.jpg3.jpg" ]; then
       echo "The server generated the proper payload: $PAYLOAD_ORIGINAL"
fi
cd ..
 
sleep 300

curl -XDELETE localhost:8080/v1/problems/$ID

[ -d payload ] || mkdir payload
sudo docker cp $CLIENT_DOCKER:/tmp/runs/$ID/safe.tar safe.tar
tar xf safe.tar 2>/dev/null
PAYLOAD_ENCRYPTED=$(ls | sort | perl -le '$r = ""; while(<>){if(/.*\.jpg/){chomp(); $r=$r.$_;}} print $r;')
if [ "x$PAYLOAD_ENCRYPTED" == "x1.jpg2.jpg3.jpg" ]; then
       echo "The client produced the proper payload: $PAYLOAD_ENCRYPTED"
fi
cd ..
