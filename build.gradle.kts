plugins {
    `java-library`
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.ktlint)
    alias(libs.plugins.versions)
    `maven-publish`
}

val cliVersion = providers.gradleProperty("version").orNull?.trim()

group = "com.github.jactor-rises"
version = cliVersion.takeIf { !it.isNullOrBlank() } ?: "0.0.0-SNAPSHOT"

dependencies {
    implementation(libs.springdoc.openapi.ui)
    implementation(libs.fasterxml.core)
    implementation(libs.fasterxml.datatype)

    testImplementation(libs.assertk)
    testImplementation(libs.junit.jupiter)
    testImplementation(libs.junit.platform.suite)
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(libs.versions.jvm.get().toInt())
    }
    withSourcesJar()
}

repositories {
    mavenCentral()
}

kotlin {
    compilerOptions {
        freeCompilerArgs.addAll("-Xjsr305=strict", "-Xannotation-default-target=param-property")
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
    testLogging {
        events("passed", "skipped", "failed")
        exceptionFormat = org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
    }
}

tasks.register("printVersion") {
    doLast {
        println(project.version)
    }
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])
            artifactId = rootProject.name
            groupId = "com.github.jactor-rises"
            version = project.version.toString()

            pom {
                name.set("jactor-shared")
                description.set("Shared library for Jactor Rises")
                url.set("https://github.com/jactor-rises/jactor-shared")

                licenses {
                    license {
                        name.set("The Apache License, Version 2.0")
                        url.set("https://www.apache.org/licenses/LICENSE-2.0.txt")
                    }
                }

                developers {
                    developer {
                        id.set("jactor-rises")
                        name.set("Jactor Rises")
                        email.set("contact@jactor-rises.com")
                    }
                }

                scm {
                    connection.set("scm:git:https://github.com/jactor-rises/jactor-shared.git")
                    developerConnection.set("scm:git:https://github.com/jactor-rises/jactor-shared.git")
                    url.set("https://github.com/jactor-rises/jactor-shared")
                }
            }
        }
    }

    repositories {
        maven {
            name = "GitHubPackages"
            val repo = System.getenv("GITHUB_REPOSITORY")
            url = uri("https://maven.pkg.github.com/$repo")

            credentials {
                username = System.getenv("GITHUB_ACTOR")
                password = System.getenv("GITHUB_TOKEN")
            }
        }
    }
}
