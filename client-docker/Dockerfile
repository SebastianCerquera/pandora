FROM debian:9

RUN apt-get update && apt-get install -y openjdk-8-jdk

COPY client.jar /opt/client.jar 
COPY entrypoint.sh /opt/entrypoint.sh

RUN chmod +x /opt/entrypoint.sh 

ENTRYPOINT ["/opt/entrypoint.sh"]
CMD ["client"]
