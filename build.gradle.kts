plugins {
    id("dev.architectury.loom") version "1.10-SNAPSHOT"// apply false
    id("architectury-plugin") version "3.4-SNAPSHOT"
    id("com.github.johnrengelman.shadow") version "8.1.1"// apply false
    id("java")
    id("maven-publish")
}

buildscript {
    repositories {
        mavenCentral()
    }
}

architectury {
    minecraft = project.findProperty("minecraft_version") as String
}

allprojects {
    group = rootProject.findProperty("mod_maven_group") as String
    version = rootProject.findProperty("mod_version") as String
}

subprojects {
    apply(plugin = "dev.architectury.loom")
    apply(plugin = "architectury-plugin")
    apply(plugin = "maven-publish")

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

    loom {
        silentMojangMappingsLicense()
    }

    dependencies {
        "minecraft"("net.minecraft:minecraft:${rootProject.findProperty("minecraft_version")}")
        mappings(loom.layered {
            officialMojangMappings()
            parchment(
                "org.parchmentmc.data:parchment-${rootProject.findProperty("parchment_minecraft_version")}:${
                    rootProject.findProperty(
                        "parchment_version"
                    )
                }@zip"
            )
        })
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
//                artifactId = project.archivesName.get()
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
