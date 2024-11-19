plugins {
    id("java")
}

group = "me.lordnumberwang"
version = "1.0-SNAPSHOT"

tasks.named<Jar>("jar") {
    manifest {
        attributes["Main-Class"] = "me.lordnumberwang.languageapp.SayHello"
    }
}

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks.test {
    useJUnitPlatform()
}