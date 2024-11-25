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
