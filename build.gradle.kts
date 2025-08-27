plugins {
    kotlin("jvm") version "2.1.21"
    application
}

group = "com.aznos"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {

}

application {
    mainClass.set("MainKt")
}

kotlin {
    jvmToolchain(21)
}