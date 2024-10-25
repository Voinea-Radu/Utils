plugins {
    id("java")
    id("java-library")
    id("maven-publish")
}

group = libs.versions.group.get()
version = libs.versions.version.get()

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
    compileOnly(libs.lombok)
    annotationProcessor(libs.lombok)
    testCompileOnly(libs.lombok)
    testAnnotationProcessor(libs.lombok)

    compileOnly(libs.jetbrains.annotations)
    annotationProcessor(libs.jetbrains.annotations)
    testCompileOnly(libs.jetbrains.annotations)
    testAnnotationProcessor(libs.jetbrains.annotations)

    // Tests
    testImplementation(platform(libs.junit.bom))
    testImplementation(libs.junit.jupiter)
    testImplementation(libs.slf4j.log4j)
    testImplementation(libs.log4j)
}

tasks {
    java {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }

    test {
        useJUnitPlatform()
        testLogging {
            events("passed", "skipped", "failed")
        }
    }
}


publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["java"])
        }
    }

    repositories {
        if (project.properties["com.voinearadu.publish"] == "true") {
            maven(url = (project.findProperty("com.voinearadu.url") ?: "") as String) {
                name = "VoineaRaduRepository"
                credentials(PasswordCredentials::class) {
                    username = (project.findProperty("com.voinearadu.auth.username") ?: "") as String
                    password = (project.findProperty("com.voinearadu.auth.password") ?: "") as String
                }
            }
        }
        if (project.properties["generic.publish"] == "true") {
            maven(url = (project.findProperty("generic.url") ?: "") as String) {
                name = "GenericRepository"
                credentials(PasswordCredentials::class) {
                    username = (project.findProperty("generic.auth.username") ?: "") as String
                    password = (project.findProperty("generic.auth.password") ?: "") as String
                }
            }
        }
    }
}

