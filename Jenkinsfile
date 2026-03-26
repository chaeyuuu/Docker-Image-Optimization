pipeline {
    agent any

    environment {
        DOCKER_HUB_ID  = "caminobelllo"
        IMAGE_NAME     = "${DOCKER_HUB_ID}/docker-optimization"
        IMAGE_TAG      = "${BUILD_NUMBER}"
        CONTAINER_NAME = "spring-app"
        APP_PORT       = "8080"
        CREDENTIALS_ID = "dockerhub-credentials"
    }

    stages {

        stage('Checkout') {
            steps {
                echo "=== [1/5] 소스코드 체크아웃 ==="
                checkout scm
            }
        }

        stage('Build JAR') {
            steps {
                echo "=== [2/5] Gradle 빌드 ==="
                sh 'chmod +x gradlew'
                sh '''
                    START=$(date +%s)
                    ./gradlew bootJar --no-daemon -x test
                    END=$(date +%s)
                    echo "▶ Gradle 빌드 소요 시간: $((END - START))초"
                '''
            }
            post {
                success { sh 'ls -lh build/libs/*.jar' }
            }
        }

        stage('Docker Build') {
            steps {
                echo "=== [3/5] Docker 이미지 빌드 ==="
                withCredentials([
                    file(credentialsId: 'application-yml', variable: 'APP_YML')
                ]) {
                    sh '''
                        cp $APP_YML ./application.yml

                        START=$(date +%s)
                        docker build \
                            -t ${IMAGE_NAME}:${IMAGE_TAG} \
                            -t ${IMAGE_NAME}:latest \
                            -f Dockerfile .
                        END=$(date +%s)
                        echo "▶ Docker 빌드 소요 시간: $((END - START))초"
                    '''
                    sh "docker images ${IMAGE_NAME}:${IMAGE_TAG} --format 'table {{.Repository}}\\t{{.Tag}}\\t{{.Size}}'"
                }
            }
        }

        stage('Docker Push') {
            steps {
                echo "=== [4/5] Docker Hub Push ==="
                withCredentials([usernamePassword(
                    credentialsId: "${CREDENTIALS_ID}",
                    usernameVariable: 'DOCKER_USER',
                    passwordVariable: 'DOCKER_PASS'
                )]) {
                    sh '''
                        echo $DOCKER_PASS | docker login -u $DOCKER_USER --password-stdin
                        docker push ${IMAGE_NAME}:${IMAGE_TAG}
                        docker push ${IMAGE_NAME}:latest
                    '''
                }
            }
        }

        stage('Deploy') {
            steps {
                echo "=== [5/5] 배포 ==="
                withCredentials([
                    string(credentialsId: 'db-username', variable: 'DB_USER'),
                    string(credentialsId: 'db-password', variable: 'DB_PASS')
                ]) {
                    sh '''
                        docker stop ${CONTAINER_NAME} || true
                        docker rm   ${CONTAINER_NAME} || true

                        docker run -d \
                            --name ${CONTAINER_NAME} \
                            --restart unless-stopped \
                            -p ${APP_PORT}:8080 \
                            -e DB_HOST=172.17.0.1 \
                            -e DB_PORT=3307 \
                            -e DB_USERNAME=${DB_USER} \
                            -e DB_PASSWORD=${DB_PASS} \
                            ${IMAGE_NAME}:${IMAGE_TAG}
                    '''
                }
            }
        }

        stage('Cleanup') {
            steps {
                sh 'docker image prune -f'
            }
        }
    }

    post {
        success { echo "✅ 배포 성공: ${IMAGE_NAME}:${IMAGE_TAG}" }
        failure {
            sh "docker logs ${CONTAINER_NAME} --tail=50 || true"
            echo "❌ 파이프라인 실패"
        }
        always { sh 'docker logout || true' }
    }
}