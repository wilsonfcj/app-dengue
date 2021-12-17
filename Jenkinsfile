pipeline {
	agent any
	environment {
		APP_NAME = 'test'
	}
	options {
		// Stop the build early in case of compile or test failures
		skipStagesAfterUnstable()
	}
	
	stages {
		
		stage('Detect build type') {
			steps {
				script {
					if (env.BRANCH_NAME == 'develop' || env.CHANGE_TARGET == 'develop') {
						env.BUILD_TYPE = 'debug'
					} else if (env.BRANCH_NAME == 'master' || env.CHANGE_TARGET == 'master') {
						env.BUILD_TYPE = 'release'
					}
				}
			}
		}
		
		stage('Build APK') {
		  steps {
			// Finish building and packaging the APK
			sh './gradlew assembleDev'

			// Archive the APKs so that they can be downloaded from Jenkins
			archiveArtifacts '**/*.apk'
		  }
		}
		
	}

		
	
	
}