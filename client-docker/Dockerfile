FROM debian:9

RUN apt-get update && apt-get install -y openjdk-8-jdk

COPY client.jar /opt/client.jar 
COPY entrypoint.sh /opt/entrypoint.sh

RUN chmod +x /opt/entrypoint.sh 

## TODO el valor se pasa como una variable de entorno cuando se crea el contenedor, estoy quemando un valor porque no puedo conocer el valor real.
## WORKDIR /tmp/runs

ENTRYPOINT ["bash", "/opt/entrypoint.sh", "client"]
