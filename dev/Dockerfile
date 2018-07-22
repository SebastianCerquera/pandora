FROM ubuntu:18.04

ENV  APP_USER  development

RUN apt-get update && apt-get install -y build-essential software-properties-common byobu curl git htop man unzip vim wget gedit openssh-server openssh-client xclip && rm -rf /var/lib/apt/lists/*

RUN apt-get update && apt-get install -y openjdk-11-jdk  maven umlet gradle && rm -rf /var/lib/apt/lists/*

COPY entrypoint.sh /opt/entrypoint.sh

RUN chmod +x /opt/entrypoint.sh

ENTRYPOINT ["/opt/entrypoint.sh"]
 
CMD ["sleep", "infinity"]