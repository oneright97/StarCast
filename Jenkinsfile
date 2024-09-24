pipeline {
    agent any  
		
    tools {
      jdk ("jdk21")
    }

    environment {
        SSH_CREDENTIALS_ID = "${env.SSH_CREDENTIALS_ID}"
        REMOTE_SERVER = "${env.REMOTE_SERVER}"
        FRONTEND_DIR = 'frontend'
        BACKEND_DIR = 'backend/starcast'
        DOCKER_COMPOSE_FILE = 'docker-compose.yml'  
    }

    stages {

        stage('Print Branch Info') {
            steps {
                script {
                    echo "Current GIT_BRANCH: ${env.GIT_BRANCH}"
                    def branch = sh(script: "git rev-parse --abbrev-ref HEAD", returnStdout: true).trim()  
                    echo "Current branch: ${branch}"
                    echo "REMOTE_SERVER: ${env.REMOTE_SERVER}"
                    echo "BRANCH_NAME: ${env.BRANCH_NAME}"
                }
            }
        }

        stage('Checkout Code') {
            when {
                anyOf {
                    expression { env.GIT_BRANCH == 'release' } 
                    expression { env.GIT_BRANCH == 'master' }  
                    expression { env.GIT_BRANCH == 'front-dev' }  
                    expression { env.GIT_BRANCH == 'back-dev' }  
                }
            } 
            steps {
                script {
                    if (env.GIT_BRANCH == 'release') {
                        git branch: 'release', credentialsId: 'jenkins', url: 'https://lab.ssafy.com/s11-bigdata-dist-sub1/S11P21A609.git'
                    } else if (env.GIT_BRANCH == 'master') {
                        git branch: 'master', credentialsId: 'jenkins', url: 'https://lab.ssafy.com/s11-bigdata-dist-sub1/S11P21A609.git'
                    } else if (env.GIT_BRANCH == 'front-dev') {
                        git branch: 'front-dev', credentialsId: 'jenkins', url: 'https://lab.ssafy.com/s11-bigdata-dist-sub1/S11P21A609.git'
                    } else if (env.GIT_BRANCH == 'back-dev') {
                        git branch: 'back-dev', credentialsId: 'jenkins', url: 'https://lab.ssafy.com/s11-bigdata-dist-sub1/S11P21A609.git'
                    }
                }
            }
        }

        stage('List Directory') {
            steps {
                sh 'ls -l'
                sh 'ls -l /var/jenkins_home/workspace/a609/frontend'
                sh 'ls -l /var/jenkins_home/workspace/a609/backend/starcast'
            }
        }

        stage('Build Docker Images') {
            when {
                anyOf {
                    expression { env.GIT_BRANCH == 'release' }  
                    expression { env.GIT_BRANCH == 'master' }
                    expression { env.GIT_BRANCH == 'front-dev' }
                    expression { env.GIT_BRANCH == 'back-dev' }
                }
            }
            steps {
                script {
                    sh 'docker build -t frontend:latest /var/jenkins_home/workspace/a609/frontend'

                    dir('/var/jenkins_home/workspace/a609/backend/starcast') {
                        sh 'chmod +x gradlew'
                        sh './gradlew build'
                    }

                    sh 'ls -l /var/jenkins_home/workspace/a609/backend/starcast/build/libs/'

                    sh 'docker build -t backend:latest /var/jenkins_home/workspace/a609/backend/starcast'
                }
            }
        }

        stage('Deploy to Remote Server') {
            when {
                anyOf {
                    expression { env.GIT_BRANCH == 'release' }  
                    expression { env.GIT_BRANCH == 'master' }  
                    expression { env.GIT_BRANCH == 'front-dev' }
                    expression { env.GIT_BRANCH == 'back-dev' }
              }
            }
            steps {
                script {
                    sshagent([SSH_CREDENTIALS_ID]) {  
                        sh '''
                        docker save frontend:latest | ssh -o StrictHostKeyChecking=no ${REMOTE_SERVER} 'docker load'
                        docker save backend:latest | ssh -o StrictHostKeyChecking=no ${REMOTE_SERVER} 'docker load'
                        scp -o StrictHostKeyChecking=no ${DOCKER_COMPOSE_FILE} ${REMOTE_SERVER}:/home/ubuntu  
                        ssh -o StrictHostKeyChecking=no ${REMOTE_SERVER} << EOF
                            cd /home/ubuntu
                            docker-compose -f ${DOCKER_COMPOSE_FILE} up -d  
EOF
                        '''
                    }
                }
            }
        }
    }

post {
    success {
        script {
            mattermostSend (
                color: 'good',
                channel: 'JenkinsBuild',
                endpoint: 'https://meeting.ssafy.com/hooks/e8wiuh31q3rqjjnwpyw5niprxo',
                message: """\
빌드 성공 !! 당신은 유능한 개발자입니다 :castar_build_happy:
Build Number: ${env.BUILD_NUMBER}
Commit Message: ${env.GIT_COMMIT_MESSAGE}
Committer: ${env.GIT_AUTHOR_NAME}
Branch: ${env.GIT_BRANCH.replace('origin/', '')}
<${env.BUILD_URL}|Link to build>"""
            )
        }
    }
    failure {
        script {
            mattermostSend (
                color: 'danger',
                channel: 'JenkinsBuild',
                endpoint: 'https://meeting.ssafy.com/hooks/e8wiuh31q3rqjjnwpyw5niprxo',
                message: """\
빌드 실패 !! 힘내서 고쳐주세요 :castar_build_sad:
Build Number: ${env.BUILD_NUMBER}
Commit Message: ${env.GIT_COMMIT_MESSAGE}
Committer: ${env.GIT_AUTHOR_NAME}
Branch: ${env.GIT_BRANCH.replace('origin/', '')}
<${env.BUILD_URL}|Link to build>"""
            )
        }
    }
    always {
        cleanWs()
    }
}
}