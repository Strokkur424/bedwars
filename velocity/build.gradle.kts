plugins {
    id("xyz.jpenilla.run-velocity") version "2.3.1"
}

@Suppress("VulnerableLibrariesLocal")
dependencies {
    compileOnly("com.velocitypowered:velocity-api:3.4.0-SNAPSHOT")
    annotationProcessor("com.velocitypowered:velocity-api:3.4.0-SNAPSHOT")
}

tasks.runVelocity {
    velocityVersion("3.4.0-SNAPSHOT")
    
    downloadPlugins {
        url("https://download.luckperms.net/1570/velocity/LuckPerms-Velocity-5.4.153.jar")
    }
}