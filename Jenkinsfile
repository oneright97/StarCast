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

        // 1. 브랜치 정보 출력
        stage('Print Branch Info') {
            steps {
                script {
                    def branch = sh(script: "git rev-parse --abbrev-ref HEAD", returnStdout: true).trim()
                    echo "Current branch: ${branch}"
                    env.BRANCH_NAME = branch  // 현재 브랜치 이름을 저장
                }
            }
        }

        // 2. 코드 체크아웃
        stage('Checkout Code') {
		        // when 조건으로 이런 상황일 때 진행하겠다고 정의
            when {
                anyOf {
		                // 브랜치가 develop일 경우
                    expression { env.GIT_BRANCH == 'origin/release' } 
                    // 브랜치가 master일 경우 
                    expression { env.GIT_BRANCH == 'origin/master' }  
                    //임시브랜치ㅣㅣ
                    expression { env.GIT_BRANCH == 'origin/front-dev' }
                    expression { env.GIT_BRANCH == 'origin/back-dev' }  
                }
            } 
            steps {
                script {
                    // 현재 브랜치가 develop이면 
                    // develop 브랜치에서 코드를 가져오고 
                    // master이면 master 브랜치에서 코드를 가져옴.
                    if (env.BRANCH_NAME == 'release') {
                        git branch: 'release', credentialsId: 'jenkins', url: 'https://lab.ssafy.com/s11-bigdata-dist-sub1/S11P21A609.git'
                    } else if (env.BRANCH_NAME == 'master') {
                        git branch: 'master', credentialsId: 'jenkins', url: 'https://lab.ssafy.com/s11-bigdata-dist-sub1/S11P21A609.git'
                    } else if (env.BRANCH_NAME == 'front-dev') {
                        git branch: 'front-dev', credentialsId: 'jenkins', url: 'https://lab.ssafy.com/s11-bigdata-dist-sub1/S11P21A609.git'
                    } else if (env.BRANCH_NAME == 'back-dev') {
                        git branch: 'front-dev', credentialsId: 'jenkins', url: 'https://lab.ssafy.com/s11-bigdata-dist-sub1/S11P21A609.git'
                }
            }
        }

        // 3. 디렉토리 리스트 출력
        stage('List Directory') {
            steps {
                sh 'ls -l'
                sh 'ls -l /var/jenkins_home/workspace/a609/frontend'
                sh 'ls -l /var/jenkins_home/workspace/a609/backend/starcast'
            }
        }

        // 4. Docker 이미지 빌드
        stage('Build Docker Images') {
		        // 브랜치가 release일 경우
		        // 브랜치가 master일 경우
            when {
                anyOf {
                    expression { env.GIT_BRANCH == 'origin/release' }  
                    expression { env.GIT_BRANCH == 'origin/master' }
                    expression { env.GIT_BRANCH == 'origin/front-dev' }
                    expression { env.GIT_BRANCH == 'origin/back-dev' }
                }
            }
            steps {
                script {
                    // 프론트엔드 Docker 이미지 빌드
                    sh 'docker build -t frontend:latest /var/jenkins_home/workspace/a609/frontend'

                    // 백엔드 디렉토리로 이동하여 Gradle 빌드 실행
                    dir('/var/jenkins_home/workspace/a609/backend/starcast') {
                        sh 'chmod +x gradlew'
                        sh './gradlew build'
                    }

                    sh 'ls -l /var/jenkins_home/workspace/a609/backend/starcast/build/libs/'

                              // Docker 이미지 빌드
                              sh 'docker build -t backend:latest /var/jenkins_home/workspace/a609/backend/starcast'

                }
            }
        }

        // 5. 원격 서버에 배포
        stage('Deploy to Remote Server') {
		        // 브랜치가 release일 경우
		        // 브랜치가 master일 경우
            when {
                anyOf {
                    expression { env.GIT_BRANCH == 'origin/release' }  
                    expression { env.GIT_BRANCH == 'origin/master' }  
                    expression { env.GIT_BRANCH == 'origin/front-dev' }  
                    expression { env.GIT_BRANCH == 'origin/back-dev' }  
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
        always {
            cleanWs()  // 파이프라인 실행 후 워크스페이스 정리 (불필요한 파일 삭제)
        }
    }
}

