import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.3.72"
    id("org.openjfx.javafxplugin") version "0.0.9"
}

group = "world.gregs.base.tornado"
version = "1.0"

repositories {
    mavenCentral()
    maven("https://oss.sonatype.org/content/repositories/snapshots")
    maven("https://repo.adobe.com/nexus/content/repositories/public/")
    maven(url = "https://dl.bintray.com/michaelbull/maven")
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-javafx:1.1.1")
    implementation("no.tornado:tornadofx:2.0.0-SNAPSHOT")
    implementation("com.google.guava:guava:23.6-jre")
    implementation("com.google.code.gson:gson:2.8.5")
    implementation("no.tornado:tornadofx-controlsfx:0.1")
    implementation("org.apache.commons:commons-imaging:1.0-R1534292")
    implementation("org.apache.commons:commons-compress:1.15")
    implementation("org.apache.commons:commons-compress:1.15")
    implementation("commons-io:commons-io:2.4")
    implementation("com.displee:rs-cache-library:6.6")
    implementation("com.michael-bull.kotlin-inline-logger:kotlin-inline-logger-jvm:1.0.2")
    implementation("io.netty:netty-buffer:4.1.51.Final")

    testImplementation("junit:junit:4.12")
}

val javafxModules = arrayOf("controls", "fxml", "graphics")

javafx {
    modules = javafxModules.map { "javafx.$it" }
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}