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
        DROPBOX_FOLDER = ""
        PROGUARD_ENABLED = ""
        JIRA_PROJECT_KEY = ""
        PROJECT_NAME = env.JOB_NAME.tokenize("/").first().replaceAll(" Android", "")
    }
	
    options {
        // Stop the build early in case of compile or test failures
        skipStagesAfterUnstable()
    }
	
}