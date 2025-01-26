rootProject.name = "bedwars"

sequenceOf("paper").forEach { 
    include(it)
    project(":$it").projectDir = file(it)
}