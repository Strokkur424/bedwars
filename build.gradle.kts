plugins {
    id("java")
}

subprojects {
    plugins.apply("java")

    group = "net.strokkur.bedwars"
    version = "1.0-SNAPSHOT"

    repositories {
        mavenCentral()
        maven("https://repo.papermc.io/repository/maven-public/")
    }

    java {
        toolchain.languageVersion = JavaLanguageVersion.of(21)
    }
    
    tasks.jar {
        doLast {
            copy {
                from(project.layout.buildDirectory.dir("libs"))
                into(rootProject.projectDir.resolve("build"))
            }
        }
    }
}