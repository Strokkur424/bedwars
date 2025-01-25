rootProject.name = "HypixelBedwars"

sequenceOf("paper", "velocity").forEach { 
    include(it)
    project(":$it").projectDir = file(it)
}