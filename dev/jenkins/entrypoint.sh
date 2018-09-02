#!/bin/bash

SECRET=$1
JENKINS_HOME="/home/jenkins"

if [ -z "$SECRET" ]; then
    echo "You should provide the jenkins secret"
    exit 1
fi

bash -x /opt/adduser.sh  jenkins

if [ "$SECRET" == "bash" ]; then
    exec bash
else
    su -c "wget http://jenkins:8080/jnlpJars/agent.jar -O /home/jenkins/agent.jar" jenkins
    
    if [ ! -d "$JENKINS_HOME/remoting" ]; then
	mkdir $JENKINS_HOME/remoting
	chown jenkins:jenkins $JENKINS_HOME/remoting
    fi
    
    ## You need to name the agent as docker-agent for this to work.
    su -c "java -jar agent.jar -jnlpUrl http://jenkins:8080/computer/docker-agent/slave-agent.jnlp -secret $SECRET -workDir /home/jenkins -failIfWorkDirIsMissing" jenkins

fi
