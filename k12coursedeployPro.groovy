pipeline {
    agent none
    environment {
        CI_PROJECT_DIR = 'E:\\k12jenkins\\bundledeploy'
        Unity_Editor = 'C:\\Program Files\\Unity2019.3.1f1\\Editor\\Unity.exe'
        Scripts_Folder = 'E:\\ci_scripts'
        
    }
    
    stages {  
        stage('Windows Export and Upload Package') {
            agent {
                    label "k12-win12-001"
            }

            stages{
                stage('Which Method to deploy?') {
                    steps {
                        input message: 'Which Method to deploy?', parameters: [string(defaultValue: '0.0.1', description: 'staging image tag', name: 'deployMethod', trim: true)]
                    }
                }
                stage('DeployStg'){
                    steps {
                        powershell """
                            cd $CI_PROJECT_DIR
                            git reset --hard HEAD
                            git pull origin master
                            python copy.py --env=onebox --dest=staging --prefix=CourseProject,CourseBundle,PlaygroundProject,PlaygroundPlugin,PlaygroundLauncher,PlaygroundLib --temp=$CI_PROJECT_DIR\\temp
                        """
                    }
                }
                stage('DeployPrd'){
                    steps {
                        powershell """
                            cd $CI_PROJECT_DIR
                            git reset --hard HEAD
                            git pull origin master
                            python copy.py --env=staging --dest=prod --prefix=CourseProject,CourseBundle,PlaygroundProject,PlaygroundPlugin,PlaygroundLauncher,PlaygroundLib --temp=$CI_PROJECT_DIR\\temp                        
                        """
                    }
                }
                stage('DeployDirectlyPrd'){
                    steps {
                        powershell """
                            cd $CI_PROJECT_DIR
                            git reset --hard HEAD
                            git pull origin master
                            python copy.py --env=onebox --dest=prod --prefix=CourseProject,CourseBundle,PlaygroundProject,PlaygroundPlugin,PlaygroundLauncher,PlaygroundLib --temp=$CI_PROJECT_DIR\\temp                        
                        """
                    }
                }
                stage('DeployPrd'){
                    steps {
                        powershell """
                            cd $CI_PROJECT_DIR
                            git reset --hard HEAD
                            git pull origin master
                            python copy.py --env=onebox --dest=prod --prefix=Standalone --temp=$CI_PROJECT_DIR/temp
                        """
                    }
                }
            }
        }
          


    }
}