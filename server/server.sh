#!/bin/bash

set -e

N=$1
BASE=$2

SRC=/usr/share/nginx/html
DEST=$BASE/backup

## Generate problem
cd $SRC


find -type d | sort | perl -lne '/(\d+)/ && print $1' > $SRC/INDEX

for d in $(find -type d | sort | perl -lne '/(\d+)/ && print $1'); do

    mkdir -p $DEST/$d

    cd $SRC/$d
    openssl genrsa -out key.txt 100 2&>/dev/null

    MODULUS=$(openssl rsa -modulus -in key.txt 2>/dev/null | perl -ne 'if(/Modulus=(.+)/){use bigint; $n = hex($1); print "$n";}')
    PUBLIC_EXPONENT=$(openssl rsa -text -in key.txt 2>/dev/null | perl -ne 'if(/publicExponent:\s+(.+)\s+\(/){print "$1";}')
    PRIVATE_EXPONENT=$(openssl rsa -text -in key.txt 2>/dev/null | perl -e '$str = do { local $/; <STDIN> }; $str =~ s/\R//g; $str =~ s/\s//g; $str =~ s/://g; if($str =~ /privateExponent(.*)prime1/){use bigint; $exp = hex($1);} print "$exp\n"')

    PRIME1=$(openssl rsa -text -in key.txt 2>/dev/null | perl -e '$str = do { local $/; <STDIN> }; $str =~ s/\R//g; $str =~ s/\s//g; $str =~ s/://g; if($str =~ /prime1(.*)\(0x.+prime2/){$exp = $1;} print "$exp\n"')
    PRIME2=$(openssl rsa -text -in key.txt 2>/dev/null | perl -e '$str = do { local $/; <STDIN> }; $str =~ s/\R//g; $str =~ s/\s//g; $str =~ s/://g; if($str =~ /prime2(.*)\(0x.+exponent1/){$exp = $1;} print "$exp\n"')

    perl -e 'use bigint; $m='"$PRIME1"'*'"$PRIME2"'; exit(1) if($m != '"$MODULUS"'); print "SUCCESS '"$d"'\n";'

    NN=$(echo "$N" | perl -F, -ane '/'"$d"':(\d+)/ && print $1')
    
    echo -e "$MODULUS" > $DEST/$d/hint
    echo -e "$NN\n$PRIME1" > $SRC/$d/KEY

    ## Encrypt payload
    cd $SRC/$d
   
    mkdir safe
    mv *.jpg safe
    tar cf safe.tar safe/
done

chmod -R 777 $SRC
chmod 777 -R $DEST
