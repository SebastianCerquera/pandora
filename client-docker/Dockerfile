FROM debian:9

COPY client.jar /opt/client.jar 
COPY entrypoint.sh /opt/entrypoint.sh

RUN chmod +x /opt/entrypoint.sh 

ENTRYPOINT ["/opt/entrypoint.sh"]
CMD ["client"]
