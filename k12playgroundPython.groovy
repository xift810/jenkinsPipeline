pipeline {
    agent {label 'k12-win12-001'}
    
    environment {
        CI_PROJECT_DIR = 'E:\\k12jenkins\\python\\playground'
        Unity_Editor = 'C:\\Program Files\\Unity2019.4.8f1\\Editor\\Unity.exe'
        // CI_COMMIT_TAG = 'python_1.0.0'
        CI_COMMIT_MESSAGE = 'test_MESSAGE_1.0.16'
        Scripts_Folder = 'E:\\ci_scripts'
        
    }

    stages {
        stage('deploy tag?') {
            steps {
                script{
                    env.CI_COMMIT_TAG = input message: 'deploy tag?', parameters: [string(defaultValue: 'python_1.0.0', description: 'ci commit tag', name: 'CI_COMMIT_TAG', trim: true)]
                }
                
            }
        }
        stage('Windows export package') {
           steps {
               powershell """
                    cd $CI_PROJECT_DIR
                    git reset --hard HEAD
                    git clean -fx
                    git pull origin dev-master
                    cd $CI_PROJECT_DIR\\ci
                    .\\windows-build.ps1  "$Unity_Editor" "$CI_PROJECT_DIR" "$CI_COMMIT_TAG" "$Scripts_Folder" UNITY_K12_PLAYGROUND_INT windows
                """
            }
        }

        stage('Mac export package') {
           steps {
               powershell """
                    cd $CI_PROJECT_DIR
                    git reset --hard HEAD
                    git clean -fx
                    cd $CI_PROJECT_DIR\\ci
                    .\\windows-build.ps1  "$Unity_Editor" "$CI_PROJECT_DIR" "$CI_COMMIT_TAG" "$Scripts_Folder" UNITY_K12_PLAYGROUND_INT osx
                """
            }
        }

        stage('UploadPlugin'){
            steps {
                powershell """
                    python $Scripts_Folder\\downloadfile.py --fileName=Python/PlaygroundLib/Launcher/Temp/Unity.InternalAPIEditorBridge.001.dll --dest=$CI_PROJECT_DIR/Unity.InternalAPIEditorBridge.001.dll
                    python $Scripts_Folder\\uploadfile.py --uploadPrefix=Python/PlaygroundLib/Launcher/$CI_COMMIT_TAG --filePath=$CI_PROJECT_DIR/Unity.InternalAPIEditorBridge.001.dll --isPublic=True
                    python $Scripts_Folder\\downloadfile.py --fileName=Python/PlaygroundPlugin/Temp/UnityK12Playground_windows.unitypackage --dest=$CI_PROJECT_DIR/UnityK12Playground_windows.unitypackage
                    python $Scripts_Folder\\uploadfile.py --uploadPrefix=Python/PlaygroundPlugin/$CI_COMMIT_TAG --filePath=$CI_PROJECT_DIR/UnityK12Playground_windows.unitypackage --isPublic=True
                    python $Scripts_Folder\\downloadfile.py --fileName=Python/PlaygroundPlugin/Temp/UnityK12Playground_osx.unitypackage --dest=$CI_PROJECT_DIR/UnityK12Playground_osx.unitypackage
                    python $Scripts_Folder\\uploadfile.py --uploadPrefix=Python/PlaygroundPlugin/$CI_COMMIT_TAG --filePath=$CI_PROJECT_DIR/UnityK12Playground_osx.unitypackage --isPublic=True
                    python $Scripts_Folder\\uploadfile.py --uploadPrefix=Python/PlaygroundPluginRaw/$CI_COMMIT_TAG --filePath=E:/PlaygroundAssetBundleCITemp/Package/UnityK12PlaygroundRaw_windows.unitypackage               
                    python $Scripts_Folder\\uploadfile.py --uploadPrefix=Python/PlaygroundPluginRaw/$CI_COMMIT_TAG --filePath=E:/PlaygroundAssetBundleCITemp/Package/UnityK12PlaygroundRaw_osx.unitypackage
             """
            }
        }

        stage('clear data'){
            steps {
                powershell """
                    cd $CI_PROJECT_DIR
                    git reset --hard HEAD
                    git clean -fx
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