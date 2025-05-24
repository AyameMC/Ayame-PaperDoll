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
    id("com.github.johnrengelman.shadow")
}

architectury {
    platformSetupLoomIde()
    fabric()

}

loom {
    accessWidenerPath.set(
        project(":common")
            .extensions
            .getByType<LoomGradleExtensionAPI>()
            .accessWidenerPath
    )
}

val common by configurations.creating {
    isCanBeResolved = true
    isCanBeConsumed = false
}

configurations.named("compileClasspath") { extendsFrom(common) }
configurations.named("runtimeClasspath") { extendsFrom(common) }
configurations.named("developmentFabric") { extendsFrom(common) }

val shadowBundle by configurations.creating {
    isCanBeResolved = true
    isCanBeConsumed = false
}

dependencies {
    modImplementation("net.fabricmc:fabric-loader:${rootProject.extra["fabric_loader_version"]}")
    modImplementation("net.fabricmc.fabric-api:fabric-api:${rootProject.extra["fabric_api_version"]}")
    modImplementation("com.terraformersmc:modmenu:${project.extra["modmenu_version"]}")

    val commonDep = project(mapOf("path" to ":common", "configuration" to "namedElements"))
    add("common", commonDep)
    configurations.named("common") {
        withDependencies {
            find { it == commonDep }?.let {
                (it as? ModuleDependency)?.isTransitive = false
            }
        }
    }

    add("shadowBundle", project(mapOf("path" to ":common", "configuration" to "transformProductionFabric")))
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
        "mod_fabric_sources_url" to project.findProperty("mod_fabric_sources_url"),
        "fabric_loader_version" to project.findProperty("fabric_loader_version"),
        "fabric_api_version" to project.findProperty("fabric_api_version"),
        "fabric_minecraft_version_range" to project.findProperty("fabric_minecraft_version_range"),
        "modmenu_version" to project.findProperty("modmenu_version"),
    )

    inputs.properties(placeholders)

    filesMatching("fabric.mod.json") {
        expand(placeholders)
    }
}

tasks.named<Jar>("sourcesJar") {
    val commonSources = project(":common").tasks.named<Jar>("sourcesJar")
    dependsOn(commonSources)
    from(commonSources.map { zipTree(it.archiveFile) })
    archiveClassifier.set("sources")
}


tasks.named<com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar>("shadowJar") {
    configurations = listOf(shadowBundle)
    archiveClassifier.set("dev-shadow")
}

tasks.named<net.fabricmc.loom.task.RemapJarTask>("remapJar") {
    dependsOn("shadowJar")
    val shadowJarTask = tasks.named<com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar>("shadowJar")
    inputFile.set(shadowJarTask.get().archiveFile)
    injectAccessWidener.set(true)
}

