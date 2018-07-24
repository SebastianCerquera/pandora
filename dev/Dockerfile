FROM ubuntu:16.04

ENV  APP_USER  development

RUN apt-get update && apt-get install -y build-essential software-properties-common byobu curl git htop man unzip vim wget gedit openssh-server openssh-client xclip && rm -rf /var/lib/apt/lists/*

RUN echo oracle-java8-installer shared/accepted-oracle-license-v1-1 select true | debconf-set-selections && add-apt-repository -y ppa:webupd8team/java && apt-get update && apt-get install -y oracle-java8-installer maven umlet && rm -rf /var/lib/apt/lists/* && rm -rf /var/cache/oracle-jdk8-installer

COPY entrypoint.sh /opt/entrypoint.sh

RUN chmod +x /opt/entrypoint.sh

ENTRYPOINT ["/opt/entrypoint.sh"]
 
CMD ["sleep", "infinity"]