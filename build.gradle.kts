plugins {
    id("java")
    id("java-library")
    id("maven-publish")
}

val _group = libs.versions.group.get()
val _version = libs.versions.version.get()

group = _group
version = _version

repositories {
    mavenCentral()
    maven("https://repository.voinearadu.dev/repository/maven-releases/")
}

dependencies {
    // Dependencies
    api(libs.gson)
    api(libs.slf4j)
    api(libs.apache.commons.compress)
    api(libs.apache.commons.lang3)
    api(libs.apache.commons.pool2)
    api(libs.apache.commons.io)
    compileOnly(libs.jedis)
    testImplementation(libs.jedis)

    // Annotations
    compileOnly("org.projectlombok:lombok:1.18.34")
    annotationProcessor("org.projectlombok:lombok:1.18.34")
    testCompileOnly("org.projectlombok:lombok:1.18.34")
    testAnnotationProcessor("org.projectlombok:lombok:1.18.34")

    compileOnly("org.jetbrains:annotations:24.1.0")
    annotationProcessor("org.jetbrains:annotations:24.1.0")
    testCompileOnly("org.jetbrains:annotations:24.1.0")
    testAnnotationProcessor("org.jetbrains:annotations:24.1.0")

    // Tests
    testImplementation(platform(libs.junit.bom))
    testImplementation(libs.junit.jupiter)
}

tasks {
    java {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }

    publishing {
        publications {
            create<MavenPublication>("maven")
        }

        repositories {
            maven(url = (project.findProperty("voinearadu.url") ?: "") as String) {
                credentials(PasswordCredentials::class) {
                    username = (project.findProperty("voinearadu.auth.username") ?: "") as String
                    password = (project.findProperty("voinearadu.auth.password") ?: "") as String
                }
            }
        }
    }

    test {
        useJUnitPlatform()
        testLogging {
            events("passed", "skipped", "failed")
        }
    }
}

