pipeline {
    agent {label 'k12-onebox1'}


    stages {
        stage('Onebox Build') {
            steps {
                sh 'cd /data/go/src/gitlab.internal.unity3d.com/k12/k12hubbackend && git pull '
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
        stage('staging Build') {
            steps {
                sh 'cd /data/go/src/gitlab.internal.unity3d.com/k12/k12hubbackend && docker build -f docker/Dockerfile --build-arg RUNTIMEENV=staging -t  unity-registry.cn-shanghai.cr.aliyuncs.com/k12/backend_stg:${stg_tag} .'
            }
        }
        stage('staging image push') {
            steps {
                sh 'docker push  unity-registry.cn-shanghai.cr.aliyuncs.com/k12/backend_stg:${stg_tag}'
            }
        }
        stage('Staging Test') {
            steps {
                echo 'Testing Staging'
            }
        }
    }
      
}