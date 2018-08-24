#!/usr/bin/perl -l

$key=`openssl genrsa 100 2> /dev/null`;
$raw = `openssl rsa -modulus 2> /dev/null <<EOF
$key
EOF`;
if($raw =~ "Modulus=(.+)"){
    use bigint;
    $n = hex($1);
    print "$n";
}

$raw = `openssl rsa -text  2> /dev/null <<EOF
$key
EOF`;

$str = $raw;
$str =~ s/\R//g;
$str =~ s/\s//g;
$str =~ s/://g;
if($str =~ /prime2(.*)\(0x.+exponent1/){
     $exp = $1;
}
print "$exp";
