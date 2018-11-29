FROM ubuntu:18.04

ENV  USER      pandora
ENV  ECLIPSE_STS https://download.springsource.com/release/STS/3.9.5.RELEASE/dist/e4.8/spring-tool-suite-3.9.5.RELEASE-e4.8.0-linux-gtk-x86_64.tar.gz

RUN apt-get update && apt-get install -y build-essential software-properties-common byobu curl git htop man unzip vim wget gedit openssh-server openssh-client xclip maven umlet openjdk-11-jdk apt-transport-https ca-certificates curl software-properties-common python

RUN curl -fsSL https://download.docker.com/linux/ubuntu/gpg | apt-key add -
RUN add-apt-repository    "deb [arch=amd64] https://download.docker.com/linux/ubuntu    $(lsb_release -cs)    stable"
RUN apt-get update && apt-get install -y docker-ce

WORKDIR /opt
RUN wget ${ECLIPSE_STS} -O eclipse.tar.gz && tar xf eclipse.tar.gz && update-alternatives --install /usr/bin/eclipse eclipse /opt/sts-bundle/sts-3.9.5.RELEASE/STS 1000 && rm /opt/eclipse.tar.gz

COPY adduser.sh /opt/adduser.sh

COPY entrypoint.sh /opt/entrypoint.sh

RUN chmod +x /opt/entrypoint.sh

ENTRYPOINT ["/opt/entrypoint.sh"]

CMD ["sleep", "infinity"]