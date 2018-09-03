#!/usr/bin/bash

JENKINS_USER=$1

MYUID=$(cat /etc/passwd | perl -lane '@F = split(":", $_); print $F[2] if($F[0] =~ "'"$JENKINS_USER"'")')

if [ -z "$MYUID" ]; then
     useradd $JENKINS_USER
     echo "$JENKINS_USER:$JENKINS_USER" | chpasswd
      
     mkdir /home/$JENKINS_USER
     mkdir /home/$JENKINS_USER/.local
     chown -R $JENKINS_USER:$JENKINS_USER /home/$JENKINS_USER
fi

SGID=$(ls -all /var/run/docker.sock | awk '{print $4}' | perl -ne '/(.*)\n/ && print $1')
FGID=$(cat /etc/group | perl -ne '/docker:x:(\d+):/ && print $1')

if [ "$FGID" != "$SGID" ]; then
    perl -pi -e 's/docker:x:'"$FGID"':(.*)\n/docker:x:'"$SGID"':$1\n/g' /etc/group
fi

usermod -aG docker $JENKINS_USER

