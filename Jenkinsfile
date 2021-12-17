pipeline {

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
    stages {
      
        stage('Copy Key Stores') {
            steps {
                script {
                    def projName = PROJECT_NAME.replaceAll(" ", "_").toLowerCase()
                    sh "cp ~/Documents/android-keystores/${projName}_release.jks ../"
                    sh "cp ~/Documents/android-keystores/${projName}_upload.jks ../"
                    sh "cp ~/Documents/android-keystores/debug.ks ../"
                }
            }
        }
        stage('Copy Local Properties') {
            steps {
                script {
                    def projName = PROJECT_NAME.replaceAll(" ", "_").toLowerCase()
                    sh "cp ~/Documents/${projName}/local.properties ."
                }
            }
        }
        stage('Setup Versions') {
            steps {
                script {
                    VERSION_NAME = sh(
                            script: './gradlew -q printVersionName',
                            returnStdout: true
                    ).trim().tokenize().last()

                    VERSION_SUFFIX = sh(
                            script: './gradlew -q printVersionSuffix',
                            returnStdout: true
                    ).trim().tokenize().last()

                    APP_VERSION_NAME = VERSION_NAME + VERSION_SUFFIX

                    VERSION_CODE = sh(
                            script: './gradlew -q printVersionCode',
                            returnStdout: true
                    ).trim().tokenize().last()

                    JIRA_PROJECT_KEY = sh(
                            script: './gradlew -q printJiraProjectKey',
                            returnStdout: true
                    ).trim().tokenize().last()

                    DROPBOX_FOLDER = "${PROJECT_NAME}/${VERSION_NAME}/${APP_VERSION_NAME}"

                    PROGUARD_ENABLED = sh(
                            script: './gradlew -q printProguardEnabled',
                            returnStdout: true
                    ).trim().tokenize().last()
                }
            }
        }
        stage('Build App') {
            steps {
                // Clean and assemble APKs
                sh './gradlew clean assembleDebug assembleRelease'

                script {
                    if (env.BRANCH_NAME.startsWith("release")) {
                        sh './gradlew bundleUpload'
                    }
                }
            }
        }

        stage('Static Analysis') {
            steps {
                echo 'Static Analysis'
                // Run Lint and analyse the results
                sh './gradlew lintProductionRelease'
                androidLint()
            }
        }
        stage('Finished') {
            steps {
                echo 'Finished'
            }
        }
    }
}