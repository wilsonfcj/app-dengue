pipeline {
  agent { 
    node { label 'android' }                    
  }

  stages {                                       
    stage('Lint & Unit Test') {
      parallel {                               
        stage('checkStyle') {
          steps {
            // We use checkstyle gradle plugin to perform this
            sh './gradlew checkStyle'
          }
        }

        stage('Unit Test') {
          steps {
            // Execute your Unit Test
            sh './gradlew testStagingDebug'
          }
        }
      }
    }

   
    stage('Deploy') {
		  steps {
			script {                                                        
			  if (currentBuild.result == null         
				  || currentBuild.result == 'SUCCESS') {  
				 if(env.BRANCH_NAME ==~ /master/) {
				   // Deploy when the committed branch is master (we use fastlane to complete this)     
				   sh 'fastlane app_deploy'
			  }
			}
		  }
		}
	}


}