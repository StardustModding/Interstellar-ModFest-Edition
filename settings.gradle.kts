@file:Suppress("LocalVariableName")

pluginManagement {
    val kotlin_version: String by settings
    val dokka_version: String by settings
    val arch_plugin_version: String by settings
    val arch_loom_version: String by settings
    val shadow_version: String by settings

    plugins {
        id("org.jetbrains.kotlin.jvm") version kotlin_version
        id("org.jetbrains.kotlin.plugin.serialization") version kotlin_version
        id("org.jetbrains.dokka") version dokka_version
        id("architectury-plugin") version arch_plugin_version
        id("dev.architectury.loom") version arch_loom_version
        id("com.gradleup.shadow") version shadow_version
    }

    repositories {
        maven { url = uri("https://maven.fabricmc.net/") }
        maven { url = uri("https://maven.architectury.dev/") }
        maven { url = uri("https://maven.minecraftforge.net/") }
        maven { url = uri("https://maven.neoforged.net/releases") }

        mavenCentral()
        gradlePluginPortal()
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
}

include("common")
include("fabric")
include("neoforge")
