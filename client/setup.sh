#!/bin/bash

## The script should start as root

NEW_USER=pandora

useradd $NEW_USER
echo "pandora:pass+control=PANDORA" | chpasswd

mkdir /home/$NEW_USER
mkdir /home/$NEW_USER/.local
chown -R $NEW_USER:$NEW_USER /home/$NEW_USER


cat > /etc/sudoers.d/$NEW_USER <<EOF
$NEW_USER ALL=(ALL) NOPASSWD: ALL
EOF

sudo -u $NEW_USER mkdir /home/$NEW_USER/.ssh
sudo -u $NEW_USER chmod 755 /home/$NEW_USER/.ssh
sudo cp /home/ubuntu/.ssh/authorized_keys /home/$NEW_USER/.ssh/authorized_keys
sudo chown $NEW_USER:$NEW_USER /home/$NEW_USER/.ssh/authorized_keys


sudo apt-get remove docker docker-engine docker.io
sudo apt-get update
sudo apt-get install -y  \
    apt-transport-https \
    ca-certificates \
    curl \
    software-properties-common
 
curl -fsSL https://download.docker.com/linux/ubuntu/gpg | sudo apt-key add -
 
sudo apt-key fingerprint 0EBFCD88
 
sudo add-apt-repository \
   "deb [arch=amd64] https://download.docker.com/linux/ubuntu \
   $(lsb_release -cs) \
   stable"
 
sudo apt-get update
sudo apt-get install -y docker-ce


cd ~
git clone https://github.com/SebastianCerquera/pandora.git
cd pandora
bash ./client-docker/build.sh 0.0.1
