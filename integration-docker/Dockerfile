FROM pandora/dev:0.0.1

USER root
COPY simple-test.sh /opt/simple-test.sh
RUN chmod +x /opt/simple-test.sh

COPY entrypoint.sh /opt/entrypoint.sh
RUN chmod +x /opt/entrypoint.sh

USER ${USER}
ENTRYPOINT ["bash", "/opt/entrypoint.sh"]
CMD ["test"]
