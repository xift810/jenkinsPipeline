pipeline {
    agent none
    environment {
        CI_PROJECT_DIR = 'E:\\k12jenkins\\unity4kid'
        Unity_Editor = 'C:\\Program Files\\Unity2019.3.1f1\\Editor\\Unity.exe'
        Scripts_Folder = 'E:\\ci_scripts'
        //CI_COMMIT_SHA = "111111"
    }
    
    stages {  
        stage('Windows Export and Upload Package') {
            agent {
                    label "k12-win12-001"
            }
            stages{
                stage('Windows Build'){
                    steps {
                        powershell """
                            cd $CI_PROJECT_DIR
                            git reset --hard HEAD
                            git clean -fx
                            git pull origin dev-2020.3
                            cd  $CI_PROJECT_DIR\\ci
                            \$5CI_COMMIT_SHA = git log -n 1 --pretty=format:'%h'
                            .\\BuildAndUpload.ps1  "$CI_PROJECT_DIR" "\$5CI_COMMIT_SHA"  "Win64"
                        """
                    }
                }
                stage('OSX Build'){
                    steps {
                        powershell """
                            cd  $CI_PROJECT_DIR\\ci
                            \$5CI_COMMIT_SHA = git log -n 1 --pretty=format:'%h'
                            .\\BuildAndUpload.ps1  "$CI_PROJECT_DIR" "\$5CI_COMMIT_SHA"  "OSX"
                        """
                    }
                }
                // stage('Make diff'){
                //     steps {
                //         powershell """
                //             cd  $CI_PROJECT_DIR\\ci
                //             .\\makeDiff.ps1  "$CI_PROJECT_DIR"
                //         """
                //     }
                // }
                stage('clear data'){
                    steps {
                        powershell """
                            cd $CI_PROJECT_DIR
                            git reset --hard HEAD
                            git clean -fx
                        """
                    }
                }
            }
        }
          
        stage('Go to staging?') {
            steps {
                input message: 'Proceed or Abort?', parameters: [string(defaultValue: '0.0.1', description: 'staging image tag', name: 'stg_tag', trim: true)]
            }
        }
        stage('Staging Test') {
            
            steps {
                echo 'Testing Staging'
            }
        }
    }
}