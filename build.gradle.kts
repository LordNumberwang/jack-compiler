plugins {
    id("java")
    id("application")
}

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

application {
    mainClass = "me.lordnumberwang.vmcompiler.VmCompiler"
}

group = "me.lordnumberwang"
version = "0.1"

tasks.named<Jar>("jar") {
    manifest {
        attributes["Main-Class"] = "me.lordnumberwang.vmcompiler.VmCompiler"
    }
}

tasks.test {
    useJUnitPlatform()
}


tasks.named<JavaExec>("run") {
    // Run with: ./gradlew run -Pargs="/path/to/file1"
    // Multiple files sent space delimited: ./gradlew run -Pargs="/path/to/file/file.vm /path/to/file/file2.vm"
    description = "Compile Jack .vm files using gradlew run -Pargs=\"/path/to/file/file.vm /path/to/file/file2.vm\""
    if (project.hasProperty("args")) {
        args(project.properties["args"].toString().split("\\s+"))
    }
}

//tasks.named<JavaExec>("vmcompile") {
//    // Run with: ./gradlew vmcompile -Pargs="/path/to/file1"
//    // Multiple files sent space delimited: ./gradlew run -Pargs="/path/to/file/file.vm /path/to/file/file2.vm"
//    description = "Compile Jack .vm files using gradlew run -Pargs=\"/path/to/file/file.vm /path/to/file/file2.vm\""
//    if (project.hasProperty("args")) {
//        args(project.properties["args"].toString().split("\\s+"))
//    }
//}

// Example: ./gradlew vmCompile -PPath=/path/to/your/vm/files
tasks.register<JavaExec>("vmCompile") {
    group = "application"
    description = "Runs the VM compiler for either a target file or directory"

    //Default to using /input directory in resources, searching for all *.vm files

    mainClass.set("me.lordnumberwang.vmcompiler.VmCompiler")
    classpath = sourceSets["main"].runtimeClasspath

    val defaultPath = "input"
    // Get path from command line arguments or use default
    val path = if (project.hasProperty("Path")) {
        project.property("Path").toString()
    } else {
        defaultPath
    }
    args = listOf(path)
}

// Example: ./gradlew jackCompile -PPath=/path/to/your/jack/files
tasks.register<JavaExec>("jackCompile") {
    group = "application"
    description = "Runs the Jack compiler for either a target file or directory"
    //Default to using /input directory, searching for all *.jack files
    mainClass.set("me.lordnumberwang.jackcompiler.JackCompiler")
    classpath = sourceSets["main"].runtimeClasspath

    val defaultPath = "input"
    args = listOf(project.properties.getOrDefault("jackPath", defaultPath) as String)
}

