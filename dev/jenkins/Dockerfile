FROM jenkins/jnlp-slave
USER root
RUN apt-get update && apt-get install -y apt-transport-https ca-certificates curl software-properties-common
RUN curl -fsSL https://download.docker.com/linux/debian/gpg | apt-key add -
 
RUN add-apt-repository    "deb [arch=amd64] https://download.docker.com/linux/debian    $(lsb_release -cs)    stable"
 
RUN apt-get update && apt-get install -y docker-ce

COPY entrypoint.sh /opt/entrypoint.sh
RUN chmod +x /opt/entrypoint.sh

COPY adduser.sh /opt/adduser.sh

ENTRYPOINT ["/opt/entrypoint.sh"]

USER jenkins
CMD ["bash"]
