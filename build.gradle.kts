buildscript {
    repositories {
        mavenCentral()
        maven(url = "https://oss.sonatype.org/content/repositories/snapshots")
        gradlePluginPortal()
    }
}

plugins {
    id("org.jetbrains.intellij") version "1.13.0"
    id("org.jetbrains.kotlin.jvm") version "1.8.10"
}

intellij {
    type.set("IU")
    version.set("2022.3.2") // https://www.jetbrains.com/intellij-repository/releases com.jetbrains.intellij.idea
    plugins.addAll("org.intellij.intelliLang", "terminal", "PsiViewer:2022.3")
    pluginName.set("PowerShell")
}

kotlin {
    jvmToolchain(17)
}

sourceSets {
    main {
        java {
            srcDirs("src/main/gen")
        }
        resources {
            exclude("**.bnf", "**.flex")
        }
    }
}

group = "com.intellij.plugin"
version = "2.0.10-Omico"

repositories {
    mavenCentral()
}

dependencies {
    implementation("com.kohlschutter.junixsocket:junixsocket-common:2.3.3")
    implementation("com.kohlschutter.junixsocket:junixsocket-native-common:2.3.3")

    implementation("org.eclipse.lsp4j:org.eclipse.lsp4j:0.3.0") {
        exclude("com.google.code.gson:gson")
        exclude("com.google.guava:guava")
    }
    testImplementation(kotlin("test-junit"))
    testImplementation("junit:junit:4.13.1")
}

tasks.prepareSandbox {
    from("${project.rootDir}/language_host/current") {
        into("${intellij.pluginName.get()}/lib/")
    }
}
