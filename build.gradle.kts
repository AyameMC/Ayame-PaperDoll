import groovy.lang.Closure
import net.fabricmc.loom.api.LoomGradleExtensionAPI
import org.gradle.launcher.daemon.protocol.Build

plugins {
    id("dev.architectury.loom") version "1.10-SNAPSHOT" apply false
    id("architectury-plugin") version "3.4-SNAPSHOT"
    id("com.github.johnrengelman.shadow") version "8.1.1" apply false
    id("java")
    id("maven-publish")
}



architectury {
    minecraft = project.findProperty("minecraft_version") as String
}

allprojects {
    apply(plugin = "dev.architectury.loom")
    apply(plugin = "java")

    group = rootProject.findProperty("mod_maven_group") as String
    version = rootProject.findProperty("mod_version") as String
}

subprojects {
    apply(plugin = "com.github.johnrengelman.shadow")
    apply(plugin = "maven-publish")
    apply(plugin = "dev.architectury.loom")

    base {
        archivesName = "${rootProject.findProperty("mod_archives_name")}-${project.name}"
    }

    repositories {
        maven {
            name = "ParchmentMC"
            url = uri("https://maven.parchmentmc.org")
        }
        maven {
            name = "Terraformers"
            url = uri("https://maven.terraformersmc.com/")
        }
    }

    // defer loom configuration to afterEvaluate so Kotlin DSL recognizes it
    afterEvaluate {
        val loom = the<net.fabricmc.loom.api.LoomGradleExtensionAPI>()

        loom.silentMojangMappingsLicense()

        dependencies {
            "minecraft"("net.minecraft:minecraft:${rootProject.findProperty("minecraft_version")}")
            "mappings"(loom.layered {
                officialMojangMappings()
                parchment(
                    "org.parchmentmc.data:parchment-${rootProject.findProperty("parchment_minecraft_version")}:${rootProject.findProperty("parchment_version")}@zip"
                )
            })
        }
    }

    tasks.withType<Jar> {
        from(rootProject.file("COPYING"))
        from(rootProject.file("COPYING.LESSER"))
        from(rootProject.file("licenses")) {
            into("licenses")
        }
    }

    java {
        withSourcesJar()
    }

    tasks.withType<JavaCompile>().configureEach {
        options.release.set(21)
    }

    publishing {
        publications {
            create<MavenPublication>("mavenJava") {
                from(components["java"])
            }
        }
        repositories {
            // Define Maven repositories for publishing here if needed
        }
    }
}


repositories {
    mavenCentral()
}
