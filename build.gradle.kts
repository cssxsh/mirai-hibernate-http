plugins {
    kotlin("jvm") version "1.6.21"
    kotlin("plugin.serialization") version "1.6.21"

    id("net.mamoe.mirai-console") version "2.12.0"
    id("net.mamoe.maven-central-publish") version "0.7.1"
}

group = "xyz.cssxsh.mirai"
version = "0.1.0"

repositories {
    mavenLocal()
    mavenCentral()
}

dependencies {
    compileOnly("xyz.cssxsh.mirai:mirai-hibernate-plugin:2.4.0")
    compileOnly("net.mamoe:mirai-api-http:2.5.2")

    testImplementation(kotlin("test", "1.6.21"))
    testImplementation("net.mamoe:mirai-slf4j-bridge:1.2.0")
    testImplementation("net.mamoe:mirai-core-utils:2.12.0")
}

mirai {
    jvmTarget = JavaVersion.VERSION_11
}

kotlin {
    explicitApi()
}

tasks {
    test {
        useJUnitPlatform()
    }
}