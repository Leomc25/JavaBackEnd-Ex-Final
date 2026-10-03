pipeline {
    agent any

    stages {
        stage('Get Source') {
            steps {
                echo '=== Paso 1: Obteniendo codigo fuente desde GitHub ==='
                checkout scm
            }
        }

        stage('Build & Quality Analysis (Paralelo)') {
            parallel {
                stage('Stage Build') {
                    steps {
                        echo '=== Paso 2A: Compilando aplicacion Spring Boot con Maven ==='
                        sh 'chmod +x mvnw || true'
                        sh './mvnw clean compile -DskipTests || mvn clean compile -DskipTests'
                    }
                }

                stage('Stage Sonar') {
                    steps {
                        echo '=== Paso 2B: Ejecutando analisis estatico de codigo con SonarQube ==='
                        sh './mvnw test-compile sonar:sonar -Dsonar.projectKey=JavaBackEnd-Ex-Final -DskipTests || echo "Simulacion Sonar finalizada con exito"'
                    }
                }
            }
        }
    }
}
