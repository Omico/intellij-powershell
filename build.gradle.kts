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

val powershellModules = listOf("Plaster", "PSScriptAnalyzer")

tasks {
    powershellModules.forEach { module ->
        register("download$module") {
            group = "powershell"
            doLast {
                val dir = file(module)
                if (dir.exists()) dir.deleteRecursively()
                exec {
                    commandLine("pwsh", "-Command", "Save-Module", module, ".")
                }
            }
        }
        register("findLatest$module") {
            mustRunAfter("download$module")
            group = "powershell"
            val dirs = file(module).walk().maxDepth(1)
            if (dirs.count() > 0) outputs.dirs(dirs.last())
            doLast {
                require(file(module).exists()) { "You should run \"gradle download$module\" first." }
            }
        }
    }
}

val downloadMissingPowerShellModules by tasks.registering {
    powershellModules
        .filterNot { module -> file(module).exists() }
        .map { module -> "download$module" }
        .let { list -> dependsOn(list) }
}

tasks.prepareSandbox {
    dependsOn(downloadMissingPowerShellModules)
    from("${project.rootDir}/language_host/current") {
        into("${intellij.pluginName.get()}/lib/")
    }
    powershellModules.forEach { module ->
        from(tasks["findLatest$module"].outputs) {
            into("${intellij.pluginName.get()}/lib/LanguageHost/modules/$module")
        }
    }
}
