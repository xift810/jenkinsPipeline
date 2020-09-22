pipeline {
    agent none


    environment {
        CI_PROJECT_DIR = 'E:\\k12jenkins\\playground'
        Unity_Editor = 'C:\\Program Files\\Unity2019.3.1f1\\Editor\\Unity.exe'
        CI_COMMIT_TAG = 'test_0.1'
        Scripts_Folder = 'E:\\ci_scripts'
        
    }
    
    stages {
      stage('build and Upload on Linux and Windows'){
          parallel{
                stage('Linux Onebox PCB Build And Upload') {
                    agent {
                            label "k12-onebox1"
                    }
                    stages{
                        stage('linux build'){
                            steps {
                                sh '''
                                    cd /data/playground
                                    git pull origin dev-master
                                    ./docker/build_images.sh build onebox
                                '''
                        }
                    }
                        stage('linux upload'){
                            steps{
                                sh '''
                                        cd /data/playground
                                        ./docker/build_images.sh push onebox
                                '''
                            }
                        }
                    
                    }
                }
                stage('Windows Export and Upload Package') {
                    agent {
                            label "k12-win12-001"
                    }
                    stages{
                        stage('Windows export package'){
                          steps {
                               powershell """
                                    cd $CI_PROJECT_DIR
                                    git reset --hard HEAD
                                    git pull origin dev-master
                                    cd  $CI_PROJECT_DIR\\ci
                                    .\\windows-build.ps1 "$Unity_Editor"  "$CI_PROJECT_DIR" "$CI_COMMIT_TAG" "$Scripts_Folder" UNITY_K12_PLAYGROUND_INT
                                """
                           }
                      }
                      stage('Windows upload package'){
                          steps {
                               powershell """
                                    python $Scripts_Folder\\downloadfile.py --fileName=PlaygroundLib/Launcher/Temp/Unity.InternalAPIEditorBridge.001.dll --dest=$CI_PROJECT_DIR/Unity.InternalAPIEditorBridge.001.dll
                                    python $Scripts_Folder\\uploadfile.py --uploadPrefix=PlaygroundLib/Launcher/$CI_COMMIT_TAG --filePath=$CI_PROJECT_DIR/Unity.InternalAPIEditorBridge.001.dll --assetSync=True --assetName=playgroundRuntimeDLL --assetType=unityPackage --assetVersion=$CI_COMMIT_TAG --isPublic=True
                                    python $Scripts_Folder\\downloadfile.py --fileName=PlaygroundPlugin/Temp/UnityK12Playground.unitypackage --dest=$CI_PROJECT_DIR/UnityK12Playground.unitypackage
                                    python $Scripts_Folder\\uploadfile.py --uploadPrefix=PlaygroundPlugin/$CI_COMMIT_TAG --filePath=$CI_PROJECT_DIR/UnityK12Playground.unitypackage --assetSync=True --assetName=plugin --assetType=unityPackage --assetVersion=$CI_COMMIT_TAG --isPublic=True
                                    python $Scripts_Folder\\uploadfile.py --uploadPrefix=PlaygroundPluginRaw/$CI_COMMIT_TAG --filePath=E:/PlaygroundAssetBundleCITemp/Package/UnityK12PlaygroundRaw.unitypackage --assetName=RawPlaygroundPackage --assetSync=true --assetType=unityPackage --assetVersion=$CI_COMMIT_TAG
                                """
                           }
                      }
                      stage('clear data'){
                          steps {
                               powershell """
                                    cd $CI_PROJECT_DIR
                                    git reset --hard HEAD
                                    rm Assets/UnityK12/Editor/Icons.meta
                                    rm Assets/UnityK12/Plugins/Unity.InternalAPIEditorBridge.001.*
                                    rm Unity.InternalAPIEditorBridge.001.dll
                                """
                           }
                      }
                    }
                }
          }
      }
            
        stage('Go to staging?') {
            steps {
                input message: 'Proceed or Abort?', parameters: [string(defaultValue: '0.0.1', description: 'staging image tag', name: 'stg_tag', trim: true)]
            }
        }
        // stage('staging Build') {
        //     steps {
        //         sh 'ssh root@10.110.4.9 "cd /data/go/src/gitlab.internal.unity3d.com/k12/k12hubbackend && git pull "'
        //     }
        // }
        stage('Staging Test') {
            
            steps {
                echo 'Testing Staging'
            }
        }
        stage('Production Build') {
            steps {
                echo 'Deploying Production'
            }
        }
    }
}