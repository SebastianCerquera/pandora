#!/bin/bash

CHECK_DIR=$1

while sleep 0.1; do
   for i in $(ls $CHECK_DIR | grep tmp); do
       chmod 777 -R $CHECK_DIR/$i
   done
done
