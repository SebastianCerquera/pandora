FROM debian:9

RUN apt-get update && apt-get install -y openjdk-8-jdk perl-modules-5.24

COPY server.jar /opt/server.jar 
COPY entrypoint.sh /opt/entrypoint.sh
COPY rsagen.sh /opt/rsagen.sh

RUN chmod +x /opt/rsagen.sh
RUN chmod +x /opt/entrypoint.sh 

ENTRYPOINT ["bash", "/opt/entrypoint.sh"]
CMD ["server"]
