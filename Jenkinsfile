node('mac') {
    stage('Checkout') {
        checkout scm
    }
    if (env.BRANCH_NAME == 'master') {
        stage('Release') {
            withCredentials([usernamePassword(credentialsId: '34dc9a59-1608-4470-9860-bd19030cadbb', passwordVariable: 'GH_TOKEN', usernameVariable: 'JENKINS_USER')]) {
                withCredentials([string(credentialsId: 'npm_token', variable: 'NPM_TOKEN')]) {
                    sh 'npm run semantic-release || true'
                }
            }
        }
    }
}