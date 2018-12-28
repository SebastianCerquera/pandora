// I needed to harcode the values, i was unable to use the variables.
def DEV_VERSION="0.0.1";
def PANDORA_VERSION="";
def DOCKER_BIN="/usr/bin/docker";
def RSAGEN="/opt/rsagen.sh";

node('docker-agent'){
     stage('Prepare') {
     	checkout([ 
             $class: 'GitSCM',
             branches: [[
                 name: 'development'
             ]],
             doGenerateSubmoduleConfigurations: false,
             submoduleCfg: [],
             userRemoteConfigs: [[
                 url: 'git@192.168.3.102:/git/pandora'
             ]]
         ]);
	script {
             sh 'find ./ -type f -iname "*.sh" -exec chmod +x {} \\;'
             sh 'chmod 777 -R .'


             echo "Building Dev"
             sh 'bash ./dev/development/build.sh 0.0.5'

             echo "Building Metadata dummy"
             sh 'bash ./integration-docker/metadata-dummy/build.sh 0.0.1'
             
             echo "Building Server"
             sh 'bash ./server-docker/build.sh 0.0.5'

             echo "Building Client"
             sh 'bash ./client-docker/build.sh 0.0.5'

             echo "Deploying Metadata";
             sh '/usr/bin/docker run -d --name metadata-dummy -t pandora/metadata-dummy:0.0.1 metadata;'

             echo "Deploying Server";
             sh '/usr/bin/docker run -d --name server-jenkins -e RSAGEN=/opt/rsagen.sh -e CLIENT_TIMEOUT=60 -t pandora/server:stable server;'
             sh 'sleep 60'

             echo "Deploying Client 1";
	     sh '/usr/bin/docker run -d --name client-jenkins-1 -e JOB_DELAY=15 -e PROFILE=default -e SERVER_ENDPOINT=http://pandora:8080 -e TARGET_FOLDER=/tmp/runs -e AMAZON_METADATA=http://dummy:5200/public-hostname --link server-jenkins:pandora --link metadata-dummy:dummy -t pandora/client:stable client;'

             echo "Deploying Client 2";
	     sh '/usr/bin/docker run -d --name client-jenkins-2 -e JOB_DELAY=45 -e PROFILE=default -e SERVER_ENDPOINT=http://pandora:8080 -e TARGET_FOLDER=/tmp/runs -e AMAZON_METADATA=http://dummy:5200/public-hostname --link server-jenkins:pandora --link metadata-dummy:dummy -t pandora/client:stable client;'

             echo "Building Test"
	     sh 'bash ./integration-docker/build.sh 0.0.5'

             echo "Running Test"
             sh 'sleep 60'
	     sh '/usr/bin/docker run --rm -v /var/run/docker.sock:/var/run/docker.sock --link server-jenkins:pandora -t pandora/test:0.0.5 test'

             echo "Destroying Server";
	     sh '/usr/bin/docker stop server-jenkins;'
	     sh '/usr/bin/docker rm server-jenkins;'

             echo "Destroying Client ";
             //The test already shuts down the containers.
             //sh '/usr/bin/docker stop client-jenkins-1;'
	     sh '/usr/bin/docker rm client-jenkins-1;'
             //sh '/usr/bin/docker stop client-jenkins-2;'
	     sh '/usr/bin/docker rm client-jenkins-2;'

             echo "Destroying Dummy ";
	     sh '/usr/bin/docker stop metadata-dummy;'
	     sh '/usr/bin/docker rm metadata-dummy;'
         };
         deleteDir();
     };
}
