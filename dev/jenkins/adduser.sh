#!/usr/bin/bash

NEW_USER=$1
JENKINS_USER=$2

useradd $NEW_USER
echo "$NEW_USER:$NEW_USER" | chpasswd

mkdir /home/$NEW_USER
mkdir /home/$NEW_USER/.local
chown -R $NEW_USER:$NEW_USER /home/$NEW_USER

usermod -aG docker $JENKINS_USER
GID=$(ls -all /var/run/docker.sock | awk '{print $4}')
perl -pi -e 's/docker:x:\d+:'"$JENKINS_USER"'/docker:x:'"$GID"':'"$JENKINS_USER"'/g' /etc/group
