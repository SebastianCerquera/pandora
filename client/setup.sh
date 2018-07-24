#!/bin/bash

sudo su -

NEW_USER=$1

useradd $NEW_USER
echo "pandora:pass+control=PANDORA" | chpasswd

mkdir /home/$NEW_USER
mkdir /home/$NEW_USER/.local
chown -R $NEW_USER:$NEW_USER /home/$NEW_USER


cat > /etc/sudoers.d/pandora <<EOF
pandora ALL=(ALL) NOPASSWD: ALL
EOF

mkdir .ssh
chmod 755 .ssh/
sudo cp /home/ubuntu/.ssh/authorized_keys ~/.ssh/
cd .ssh/
sudo chown pandora:pandora authorized_keys


apt-get remove docker docker-engine docker.io
apt-get update
apt-get install -y  \
    apt-transport-https \
    ca-certificates \
    curl \
    software-properties-common
 
curl -fsSL https://download.docker.com/linux/ubuntu/gpg | apt-key add -
 
apt-key fingerprint 0EBFCD88
 
add-apt-repository \
   "deb [arch=amd64] https://download.docker.com/linux/ubuntu \
   $(lsb_release -cs) \
   stable"
 
apt-get update
apt-get install -y docker-ce


su pandora -


cd ~
git clone https://github.com/SebastianCerquera/pandora.git
cd pandora
bash ./client-docker/build.sh 0.0.1
