plugins {
    `java-library`
}

group = "com.github.zimolab"
version = "0.1.1-SNAPSHOT"

repositories {
    mavenCentral()
}

java {
    withSourcesJar()
    withJavadocJar()
}