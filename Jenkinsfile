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
                 name: 'master'
             ]],
             doGenerateSubmoduleConfigurations: false,
             submoduleCfg: [],
             userRemoteConfigs: [[
                 url: 'https://github.com/SebastianCerquera/pandora.git'
             ]]
         ]);
	script {
             sh 'find ./ -type f -iname "*.sh" -exec chmod +x {} \\;'
             sh 'chmod jenkins:jenkins -R .'

             echo "Building Dev"
             sh 'bash ./dev/development/build.sh 0.0.1'
             
             echo "Building Server"
             sh 'bash ./server-docker/build.sh 0.0.1'

             echo "Building Client"
             sh 'bash ./client-docker/build.sh 0.0.1'

             echo "Deploying Server";
             sh '/usr/bin/docker run -d --name server-jenkins -e RSAGEN=/opt/rsagen.sh -t pandora/server:stable server;'

             echo "Deploying Client";
	     sh '/usr/bin/docker run -d --name client-jenkins -e JOB_DELAY=60 -e SERVER_ENDPOINT=http://pandora:8080 -e TARGET_FOLDER=/tmp/runs --link server-jenkins:pandora -t pandora/client:stable client;'

             echo "Building Test"
             sh 'sleep 60'
	     sh 'bash ./integration-docker/build.sh 0.0.1'

             echo "Running Test"
	     sh '/usr/bin/docker run --rm -v /var/run/docker.sock:/var/run/docker.sock --link server-jenkins:pandora -t thepandorasys/test:0.0.2 test'

             echo "Destroying Server";
	     sh '/usr/bin/docker stop server-jenkins;'
	     sh '/usr/bin/docker rm server-jenkins;'

             echo "Deploying ";
	     sh '/usr/bin/docker stop client-jenkins;'
	     sh '/usr/bin/docker rm client-jenkins;'

             echo "Push images";
             sh 'bash ./server-docker/push.sh'
	     sh 'bash ./client-docker/push.sh'
         };
     };
}
