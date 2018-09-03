FROM thepandorasys/dev:0.0.2

ENV  USER      pandora
USER root

RUN mkdir /opt/integration

COPY simple-test.sh /opt/integration/simple-test.sh
RUN chmod +x /opt/integration/simple-test.sh


COPY entrypoint.sh /opt/integration/entrypoint.sh
RUN chmod +x /opt/integration/entrypoint.sh

ENTRYPOINT ["bash", "-x", "/opt/integration/entrypoint.sh"]
CMD ["test"]
