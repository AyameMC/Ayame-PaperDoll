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

plugins {
    id("com.gradleup.shadow") version "9.2.2"
}

architectury {
    platformSetupLoomIde()
    neoForge()
}

loom {
    accessWidenerPath.set(project(":common").loom.accessWidenerPath)
}

val common by configurations.creating {
    isCanBeResolved = true
    isCanBeConsumed = false
}

configurations.named("compileClasspath") { extendsFrom(common) }
configurations.named("runtimeClasspath") { extendsFrom(common) }
configurations.named("developmentNeoForge") { extendsFrom(common) }

val shadowBundle by configurations.creating {
    isCanBeResolved = true
    isCanBeConsumed = false
}

repositories {
    maven {
        name = "NeoForged"
        url = uri("https://maven.neoforged.net/releases")
    }
}

dependencies {
    add("neoForge", "net.neoforged:neoforge:${rootProject.extra["neoforge_version"]}")

    common(project(path = ":common")) { isTransitive = false }

    shadowBundle(
        project(
            path = ":common",
            configuration = "transformProductionNeoForge"
        )
    ) { isTransitive = false }
}

tasks.named<ProcessResources>("processResources") {
    val placeholders = mapOf(
        "mod_license" to project.findProperty("mod_license"),
        "mod_version" to project.version,
        "mod_id" to project.findProperty("mod_id"),
        "mod_name" to project.findProperty("mod_name"),
        "mod_homepage_url" to project.findProperty("mod_homepage_url"),
        "mod_description" to project.findProperty("mod_description"),
        "mod_mixin_config" to project.findProperty("mod_mixin_config"),
        "mod_issues_url" to project.findProperty("mod_issues_url"),
        "neoforge_version" to project.findProperty("neoforge_version"),
        "neoforge_minecraft_version_range" to project.findProperty("neoforge_minecraft_version_range"),

        "version" to project.version
    )

    inputs.properties(placeholders)

    filesMatching("META-INF/neoforge.mods.toml") {
        expand(placeholders)
    }
}

tasks.named<Jar>("jar") {
    archiveClassifier.set("raw")
}

configurations {
    apiElements {
        outgoing.artifacts.clear()
        outgoing.artifact((tasks.named("shadowJar")))
    }
    runtimeElements {
        outgoing.artifacts.clear()
        outgoing.artifact((tasks.named("shadowJar")))
    }
}


tasks.named<com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar>("shadowJar") {
    dependsOn((tasks.named("jar")))
    configurations = listOf(shadowBundle)
    archiveClassifier.set(null as String?)

    from(zipTree(tasks.named<Jar>("jar").get().archiveFile.get()))
}