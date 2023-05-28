pipeline {
    agent any
    environment {
        TARGET_HOST = "root@172.16.210.24"
    }
    
    stages() {
        stage('git clone') {
            steps() {
                slackSend (
                    channel: '#jenkins', 
                    color: '#FFFF00', 
                    message: "STARTED: Job ${env.JOB_NAME}"
                )
                git branch: 'master', credentialsId: 'git-kjk7212', url: 'https://github.com/KEA-SWAVE-SURVEY/Back-end/'
            }
        }
        
        stage('build') {
            steps {
                 sh "chmod +x gradlew"
                 sh "./gradlew clean bootJar"
                 slackSend (
                     channel: '#jenkins', 
                     color: '#00FF00', 
                     message: "SUCCESS: build ${env.JOB_NAME}"
                 )
            }
        }
        
        stage('build & push docker image') {
            steps {
                script{
                    image = docker.build("kjk7212/back")
                    docker.withRegistry('https://registry.hub.docker.com/repository/docker/kjk7212/back', 'docker-hub-credentials') {
                        image.push()
                    }
                slackSend (
                    channel: '#jenkins', 
                    color: '#00FF00', 
                    message: "SUCCESS: build docker image ${env.JOB_NAME}"
                )
                }
            }
        }
        stage('was deployment') {
            steps{
                sshagent(['WS']) {
                sh "ssh -o StrictHostKeyChecking=no ${TARGET_HOST} 'docker stop back'"
                sh "ssh -o StrictHostKeyChecking=no ${TARGET_HOST} 'docker rm back'"
                sh "ssh -o StrictHostKeyChecking=no ${TARGET_HOST} 'docker rmi kjk7212/back'"
                sh "ssh -o StrictHostKeyChecking=no ${TARGET_HOST} 'docker pull kjk7212/back'"
                sh "ssh -o StrictHostKeyChecking=no ${TARGET_HOST} 'docker run --name back -d -p 80:8080 kjk7212/back'"
                }
            }
        }
    }
    post {
        success {
            slackSend (
                channel: '#jenkins', 
                color: '#00FF00', 
                message: "SUCCESS: Job ${env.JOB_NAME}"
            )
        }
        failure {
            slackSend (
                channel: '#jenkins', 
                color: '#FF0000', 
                message: "FAIL: Job ${env.JOB_NAME}"
            )
        }
    }
}
