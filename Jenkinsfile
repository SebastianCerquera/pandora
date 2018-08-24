pipeline {
  agent { label 'app-agent' }

  stages {

    stage('prepare') {
      steps {
        // Project with utilities
        checkout([
            $class: 'GitSCM',
            branches: [[
                name: 'master'
            ]],
            doGenerateSubmoduleConfigurations: false,
            extensions: [[
                $class: 'RelativeTargetDirectory',
                relativeTargetDir: './builder'
            ]],
            submoduleCfg: [],
            userRemoteConfigs: [[
                url: 'https://github.com/SebastianCerquera/pandora.git'
            ]]
        ])
        script {
            sh 'find ./ -type f -iname "*.sh" -exec chmod +x {} \\;'
        }
      }
    }

    stage ('Build') {
        parallel{
          stage ('Build Server') {
            steps {
                script {
                    echo "Building Server"
                }
            }
          }
          stage ('Build Client') {
            steps{
                script {
                    echo "Building Client"
                }
            }
          }
        }
    }

  post {
      always {
          archiveArtifacts '**/**/*-*.jar'
          deleteDir()
      }
  }
}