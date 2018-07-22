FROM nginx:stable

ENV BASE /opt/control

RUN apt-get update && apt-get install -y openssl perl-modules-5.24 gpg

COPY server.sh /opt/server.sh
COPY entrypoint.sh /opt/entrypoint.sh

RUN chmod +x /opt/server.sh
RUN chmod +x /opt/entrypoint.sh
RUN mkdir -p $BASE/runs && mkdir -p $BASE/backup

RUN echo "\ndaemon off;" >> /etc/nginx/nginx.conf

WORKDIR $BASE/runs

ENTRYPOINT ["bash", "/opt/entrypoint.sh"]