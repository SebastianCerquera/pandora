FROM pandora/dev:0.0.1

COPY simple-test.sh /opt/simple-test.sh
RUN chmod +x /opt/simple-test.sh

COPY entrypoint.sh /opt/entrypoint.sh
RUN chmod +x /opt/entrypoint.sh 

ENTRYPOINT ["bash", "/opt/entrypoint.sh", "test"]
