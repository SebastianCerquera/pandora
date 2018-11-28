FROM ubuntu:18.04

RUN apt-get update && apt-get install -y python curl
RUN curl "https://bootstrap.pypa.io/get-pip.py" -o "get-pip.py" &&     python get-pip.py &&     pip install flask

COPY amazon.py /opt/amazon.py

COPY entrypoint.sh /opt/entrypoint.sh
RUN chmod +x /opt/entrypoint.sh

ENTRYPOINT ["bash", "-x", "/opt/entrypoint.sh"]
CMD ["metadata"]
