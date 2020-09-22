pipeline {
    agent {label 'windows'}
    
    environment {
        CI_PROJECT_DIR = 'E:\\GitLab-Runner\\builds\\AHxxqshs\\0\\k12\\playground'
        Unity_Editor = 'C:\\Program Files\\Unity2019.3.1f1\\Editor\\Unity.exe'
        // CI_COMMIT_TAG = 'case1.0.16'
        CI_COMMIT_MESSAGE = 'test_MESSAGE_1.0.16'
        Scripts_Folder = 'E:\\ci_scripts'
        
    }

    stages {
        stage('deploy tag?') {
            steps {
                script{
                    env.CI_COMMIT_TAG = input message: 'deploy tag?', parameters: [string(defaultValue: 'case1.1.0', description: 'ci commit tag', name: 'CI_COMMIT_TAG', trim: true)]
                }
                
            }
        }
        stage('Int Build') {
           steps {
               powershell """
                    cd $CI_PROJECT_DIR
                    git reset --hard HEAD
                    git pull origin dev-master
                    cd $CI_PROJECT_DIR\\ci
                    .\\windows-upload-scene-template.ps1 "$CI_PROJECT_DIR" "$Unity_Editor" "$CI_COMMIT_TAG" true
                    cd $CI_PROJECT_DIR
                    git reset --hard HEAD
                    rm  Assets/UnityK12/Editor/Icons.meta
                """
            }
        }
        
        stage('Go to Staging?') {
            steps {
                input message: 'Proceed or Abort?', parameters: [string(defaultValue: '0.0.1', description: 'staging image tag', name: 'stg_tag', trim: true)]
            }
        }
        // stage('Staging Build') {
        //     steps {
        //       powershell """
        //             cd $CI_PROJECT_DIR\\ci
        //             .\\windows-upload-scene-template.ps1 "$CI_PROJECT_DIR" "$Unity_Editor" "$CI_COMMIT_TAG" $TRUE "staging"
        //         """
        //         }
        //     }
        // }
       
    }
}