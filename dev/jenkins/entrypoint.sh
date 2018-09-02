#!/bin/bash

SECRET=$1

if [ -z "$SECRET" ]; then
    echo "You should provide the jenkins secret"
    exit 1
fi

if [ "$SECRET" == "bash" ]; then
    exec bash
else
    cd $HOME
    wget http://jenkins:8080/jnlpJars/agent.jar
    [ -d $HOME/remoting ] || mkdir $HOME/remoting
    ## You need to name the agent as docker-agent for this to work.
    java -jar agent.jar -jnlpUrl http://jenkins:8080/computer/docker-agent/slave-agent.jnlp -secret $SECRET -workDir "/home/jenkins" -failIfWorkDirIsMissing
fi
