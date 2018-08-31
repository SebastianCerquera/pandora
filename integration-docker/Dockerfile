FROM pandora/dev:0.0.1

RUN apt-get update && apt-get install curl

COPY simple-test.sh /opt/simple-test.sh
RUN chmod +x /opt/simple-test.sh

COPY entrypoint.sh /opt/entrypoint.sh
RUN chmod +x /opt/entrypoint.sh 

ENTRYPOINT ["bash", "/opt/entrypoint.sh", "test"]
