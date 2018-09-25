#!/bin/bash

SECRET=$1
JENKINS_HOME="/home/jenkins"

if [ -z "$SECRET" ]; then
    echo "You should provide the jenkins secret"
    exit 1
fi

bash -x /opt/adduser.sh  jenkins

bash /opt/permissions-daemon.sh $JENKINS_HOME/workspace &

if [ "$SECRET" == "bash" ]; then
    exec bash
else
    su -c "wget http://jenkins:8080/jnlpJars/agent.jar -O /home/jenkins/agent.jar" jenkins
    
    if [ ! -d "$JENKINS_HOME/remoting" ]; then
	mkdir $JENKINS_HOME/remoting
	chown jenkins:jenkins $JENKINS_HOME/remoting
    fi
    
    if [ ! -d "$JENKINS_HOME/workspace" ]; then
	mkdir $JENKINS_HOME/workspace
	chown jenkins:jenkins $JENKINS_HOME/workspace
    fi

    ## The deaault agent is named docker-agent
    if [ -z "${AGENT_NAME}" ]; then
	AGENT_NAME="docker-agent"
    fi

    su -c "java -jar agent.jar -jnlpUrl http://jenkins:8080/computer/$AGENT_NAME/slave-agent.jnlp -secret $SECRET -workDir /home/jenkins -failIfWorkDirIsMissing" jenkins

fi
