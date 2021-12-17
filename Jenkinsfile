pipeline {
    agent {
        // Run on a build agent where we have the Android SDK installed
        label 'android'
    }
    environment {
        // Fastlane Environment Variables
        PATH = "$HOME/.fastlane/bin:" +
                "$HOME/.rvm/gems/ruby-2.5.3/bin:" +
                "$HOME/.rvm/gems/ruby-2.5.3@global/bin:" +
                "$HOME/.rvm/rubies/ruby-2.5.3/bin:" +
                "/usr/local/bin:" +
                "$PATH"
        LC_ALL = "en_US.UTF-8"
        LANG = "en_US.UTF-8"

        VERSION_NAME = ""
        VERSION_SUFFIX = ""
        APP_VERSION_NAME = ""
        VERSION_CODE = ""
        PROGUARD_ENABLED = ""
        PROJECT_NAME = env.JOB_NAME.tokenize("/").first().replaceAll(" Android", "")
    }
	
    options {
        // Stop the build early in case of compile or test failures
        skipStagesAfterUnstable()
    }
	
	stages {
		stage('Start Build') {
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
		
		stage('Compile') {
			steps {
				// Compile the app and its dependencies
				sh './gradlew compile${env.BUILD_TYPE}Sources'
			}
		}
	 }
	
}