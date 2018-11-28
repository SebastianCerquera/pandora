FROM pandora/dev:#VERSION#

ENV  USER      pandora
USER root

RUN mkdir /opt/integration

COPY simple-test.sh /opt/integration/simple-test.sh
RUN chmod +x /opt/integration/simple-test.sh


COPY entrypoint.sh /opt/integration/entrypoint.sh
RUN chmod +x /opt/integration/entrypoint.sh

RUN apt-get update && apt-get install -y python
RUN curl "https://bootstrap.pypa.io/get-pip.py" -o "get-pip.py" && \
    python get-pip.py && \
    pip install flask

COPY amazon.py /opt/amazon.py

ENTRYPOINT ["bash", "-x", "/opt/integration/entrypoint.sh"]
CMD ["test"]
