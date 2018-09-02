#!/usr/bin/bash

JENKINS_USER=$1

usermod -aG docker $JENKINS_USER

SGID=$(ls -all /var/run/docker.sock | awk '{print $4}')
FGID=$(cat /etc/group | perl -ne '/docker:x:(\d+):/ && print $1')

if [ $FGID != $SGID ]; then
    perl -pi -e 's/docker:x:\d+:'"$JENKINS_USER"'/docker:x:'"$SGID"':'"$JENKINS_USER"'/g' /etc/group
fi
