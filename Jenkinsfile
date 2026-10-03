pipeline {
    agent any

    stages {
        stage('Get Source') {
            steps {
                echo '=== Paso 1: Obteniendo codigo fuente desde GitHub ==='
                checkout scm
            }
        }

        stage('Verificar Workspace') {
            steps {
                echo '=== Verificando archivos descargados ==='
                sh 'ls -la'
            }
        }
    }
}
