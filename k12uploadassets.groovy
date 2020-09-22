pipeline {
    agent {label 'windows'}
    
    environment {
        CI_PROJECT_DIR = 'E:\\GitLab-Runner\\builds\\AHxxqshs\\0\\k12\\playground'
        Unity_Editor = 'C:\\Program Files\\Unity2019.3.1f1\\Editor\\Unity.exe'
        CI_COMMIT_TAG = 'test_0.1'
        CI_COMMIT_MESSAGE = 'test_MESSAGE_0.1'
        Scripts_Folder = 'E:\\ci_scripts'
        
    }

    stages {
        stage('Int Build') {
           steps {
               powershell """
                    cd $CI_PROJECT_DIR\\ci
                    .\\windows-build.ps1 "$Unity_Editor"  "$CI_PROJECT_DIR" "$CI_COMMIT_TAG" "$CI_COMMIT_MESSAGE" "onebox"
                """
                }
            }
        
        stage('Go to Staging?') {
            steps {
                input message: 'Proceed or Abort?', parameters: [string(defaultValue: '0.0.1', description: 'staging image tag', name: 'stg_tag', trim: true)]
            }
        }
        stage('Staging Build') {
            steps {
               powershell """
                    cd $CI_PROJECT_DIR\\ci
                    .\\windows-build.ps1 "$Unity_Editor"  "$CI_PROJECT_DIR" "$CI_COMMIT_TAG" "$CI_COMMIT_MESSAGE" "staging"
                """
                }
            }
        }
        stage('Go to Production?') {
            steps {
                input message: 'Proceed or Abort?', parameters: [string(defaultValue: '0.0.1', description: 'staging image tag', name: 'stg_tag', trim: true)]
            }
        }
        stage('Production Build') {
            steps {
               powershell """
                    cd $CI_PROJECT_DIR\\ci
                    .\\windows-build.ps1 "$Unity_Editor"  "$CI_PROJECT_DIR" "$CI_COMMIT_TAG" "$CI_COMMIT_MESSAGE" "prod"
                """
                }
            }
        }
      
    }
}