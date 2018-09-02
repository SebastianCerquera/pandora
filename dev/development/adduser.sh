#!/usr/bin/bash

NEW_USER=$1
JENKINS_USER=$2

useradd $NEW_USER
echo "$NEW_USER:$NEW_USER" | chpasswd

mkdir /home/$NEW_USER
mkdir /home/$NEW_USER/.local
chown -R $NEW_USER:$NEW_USER /home/$NEW_USER

SGID=$(ls -all /var/run/docker.sock | awk '{print $4}' | perl -ne '/(.*)\n/ && print $1')
FGID=$(cat /etc/group | perl -ne '/docker:x:(\d+):/ && print $1')

if [ "$FGID" != "$SGID" ]; then
    perl -pi -e 's/docker:x:'"$FGID"':(.*)\n/docker:x:'"$SGID"':$1\n/g' /etc/group
fi

usermod -aG docker $JENKINS_USER
