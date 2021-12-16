pipeline {
	agent any
	environment {
		APP_NAME = 'test'
	}
	options {
		// Stop the build early in case of compile or test failures
		skipStagesAfterUnstable()
	}
	

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
	
	
}