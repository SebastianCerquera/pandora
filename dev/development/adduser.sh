#!/usr/bin/bash

NEW_USER=$1

useradd $NEW_USER
echo "$NEW_USER:$NEW_USER" | chpasswd

mkdir /home/$NEW_USER
mkdir /home/$NEW_USER/.local
chown -R $NEW_USER:$NEW_USER /home/$NEW_USER
