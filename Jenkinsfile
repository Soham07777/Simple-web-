pipeline {
    agent any

    options {
        timestamps()
        disableConcurrentBuilds()
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }
        stage('Build and integration tests') {
            steps {
                script {
                    if (isUnix()) {
                        sh 'mvn -B -ntp clean verify'
                    } else {
                        bat 'mvn -B -ntp clean verify'
                    }
                }
            }
        }
    }

    post {
        always {
            junit testResults: '**/target/surefire-reports/*.xml', allowEmptyResults: true
        }
        success {
            archiveArtifacts artifacts: 'target/*.jar', fingerprint: true
            echo 'Build and integration tests completed successfully.'
        }
        failure {
            echo 'Build failed. Review the console and test results before merging.'
        }
    }
}
