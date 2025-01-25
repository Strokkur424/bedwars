dependencies {
    compileOnly("io.papermc.paper:paper-api:1.21.4-R0.1-SNAPSHOT")
    compileOnly("org.spongepowered:configurate-yaml:4.1.2")
}

tasks.processResources {
    val props = mapOf("version" to version)
    inputs.properties(props)
    filteringCharset = "UTF-8"
    filesMatching("paper-plugin.yml") {
        expand(props)
    }
}