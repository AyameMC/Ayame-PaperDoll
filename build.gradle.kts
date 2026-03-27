/*
 *     Highly configurable PaperDoll mod. Forked from Extra Player Renderer.
 *     Copyright (C) 2024-2025  LucunJi(Original author), HappyRespawnanchor
 *
 *     This file is part of Ayame PaperDoll.
 *
 *     Ayame PaperDoll is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Lesser General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     Ayame PaperDoll is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU Lesser General Public License for more details.
 *
 *     You should have received a copy of the GNU Lesser General Public License
 *     along with Ayame PaperDoll.  If not, see <https://www.gnu.org/licenses/>.
 */

import net.fabricmc.loom.api.LoomGradleExtensionAPI


plugins {
    id("architectury-plugin") version "3.5-SNAPSHOT"
    id("dev.architectury.loom-no-remap") version "1.14-SNAPSHOT" apply false
    id("com.gradleup.shadow") version "9.2.2" apply false
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
    apply(plugin = "dev.architectury.loom-no-remap")
    apply(plugin = "maven-publish")

    base {
        archivesName = "${rootProject.findProperty("mod_archives_name")}-${project.name}"
    }

    dependencies {
        "minecraft"("com.mojang:minecraft:${rootProject.findProperty("minecraft_version")}")
    }

    tasks.withType<Jar> {
        duplicatesStrategy = DuplicatesStrategy.INCLUDE
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
        options.release.set(25)
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
