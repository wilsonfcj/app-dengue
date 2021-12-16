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
		
		
		
		stage('Build') { 
            steps { 
                sh 'make' 
            }
        }
		
		
		 stage('Test'){
            steps {
                sh 'make check'
                junit 'reports/**/*.xml' 
            }
        }
		
		
		stage('Deploy') {
            steps {
                sh 'make publish'
            }
        }
		
		
		
	}
}