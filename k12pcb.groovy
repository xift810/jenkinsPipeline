pipeline {
    agent {label 'k12-onebox1'}

    environment {
        IMG = 'unity-registry.cn-shanghai.cr.aliyuncs.com/k12/pcbserver:int'
    }
    stages {
        stage('Onebox Build') {
            steps {
                sh '''
                    cd /data/pcb
                    git pull 
                    ./build_image.sh build int 
                    ./build_image.sh push int 
                    docker rm -f $(docker ps -a -q  --filter ancestor=$IMG) || true
                    docker image prune -f
                    docker run -d -p 3005:3005 -e AES_KEY=D58BA755FF96B35A6DABA7298F7A8CE2 -e ENV=onebox --name pcbserver $IMG
                '''
            }
        }
        stage('Onebox Test') {
            steps {
                echo 'Testing Onebox'
            }
        }

        stage('Go to staging?') {
            steps {
                script {
                    env.stg_tag = input message: 'Proceed or Abort?', parameters: [string(defaultValue: '0.0.1', description: 'staging image tag', name: 'stg_tag', trim: true)]

                }
            }
        }
        
        stage('Staging Test') {
            steps {
                echo 'Testing Staging'
            }
        }
    }
      
}