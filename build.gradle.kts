import net.fabricmc.loom.api.LoomGradleExtensionAPI;


plugins {
    id("architectury-plugin") version "3.4-SNAPSHOT"
    id("dev.architectury.loom") version "1.10-SNAPSHOT" apply false
    id("com.github.johnrengelman.shadow") version "8.1.1" apply false
    id("java")
    id("maven-publish")
}

architectury {
    minecraft = project.findProperty("minecraft_version") as String
}

allprojects {
    apply(plugin = "architectury-plugin")
    apply(plugin = "java")

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

    base.archivesName = rootProject.findProperty("mod_archives_name") as String
    group = rootProject.findProperty("mod_maven_group") as String
    version = rootProject.findProperty("mod_version") as String
}

subprojects {
    apply(plugin = "dev.architectury.loom")
    apply(plugin = "com.github.johnrengelman.shadow")
    apply(plugin = "maven-publish")

    base {
        archivesName = "${rootProject.findProperty("mod_archives_name")}-${project.name}"
    }


    val loom = project.extensions.getByName<LoomGradleExtensionAPI>("loom")
    loom.silentMojangMappingsLicense()

    @Suppress("UnstableApiUsage")
    dependencies {
        "minecraft"("com.mojang:minecraft:${rootProject.findProperty("minecraft_version")}")
        "mappings"(loom.layered {
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
                artifactId = base.archivesName.get()
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
