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
