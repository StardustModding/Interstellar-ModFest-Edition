import net.fabricmc.loom.api.LoomGradleExtensionAPI
import org.jetbrains.dokka.gradle.DokkaTask
import java.net.URI

plugins {
    id("java")
    id("org.jetbrains.kotlin.jvm")
    id("org.jetbrains.kotlin.plugin.serialization")
    id("org.jetbrains.dokka")
    id("architectury-plugin")
    id("dev.architectury.loom") apply false
    id("com.gradleup.shadow")
}

architectury {
    minecraft = "minecraft_version"()
}

dependencies {
    subprojects
}

tasks.build.get().finalizedBy(tasks.named("shadowJar"))

val processedFiles = listOf(
    "fabric.mod.json",
    "META-INF/neoforge.mods.toml",
    "pack.mcmeta",
    "interstellar.mixins.json",
    "interstellar-common.mixins.json"
)

tasks.processResources {
    val props = rootProject.properties.mapValues { it.value.toString() }

    inputs.properties(props)

    filesMatching(processedFiles) {
        expand(props)
    }
}

allprojects {
    if ("native" in project.name || "grusty" in project.name) return@allprojects

    apply(plugin = "java")
    apply(plugin = "kotlin")
    apply(plugin = "kotlinx-serialization")
    apply(plugin = "java-library")
    apply(plugin = "maven-publish")
    apply(plugin = "org.jetbrains.dokka")
    apply(plugin = "architectury-plugin")
    apply(plugin = "com.gradleup.shadow")

    base.archivesName.set(rootProject.property("archives_base_name").toString())
    version = rootProject.property("mod_version").toString()
    group = rootProject.property("maven_group").toString()

    repositories {
        mavenLocal()
        mavenCentral()

        maven {
            name = "TerraformersMC Maven"
            url = uri("https://maven.terraformersmc.com/releases")
        }

        maven {
            name = "Shedaniel Maven"
            url = uri("https://maven.shedaniel.me")
        }

        maven {
            name = "Kyrptonaught Maven"
            url = uri("https://maven.kyrptonaught.dev")
        }

        maven {
            name = "BadAsIntended Maven"
            url = uri("https://maven2.bai.lol")
        }

        maven {
            name = "ParchmentMC Maven"
            url = uri("https://maven.parchmentmc.org")
        }

        maven {
            name = "tterrag Maven"
            url = uri("https://maven.tterrag.com/")
        }

        maven {
            name = "Modrinth Maven"
            url = uri("https://api.modrinth.com/maven")
        }

        maven {
            name = "BlameJared Maven"
            url = uri("https://maven.blamejared.com")
        }

        maven {
            name = "StardustModding Maven Releases"
            url = uri("https://maven.stardustmodding.org/releases")
        }

        maven {
            name = "Curse Maven"
            url = uri("https://cursemaven.com")
        }

        maven {
            name = "Fabric Maven"
            url = uri("https://maven.fabricmc.net")
        }

        maven {
            name = "NeoForge"
            url = uri("https://maven.neoforged.net/releases")
        }

        maven {
            name = "IThundxr's Magical Maven of Mysteriousness"
            url = uri("https://maven.ithundxr.dev/snapshots")
        }

        maven {
            name = "DevOS Maven"
            url = uri("https://mvn.devos.one/snapshots")
        }

        maven {
            name = "Fzzy's Amazing Incredible Tumultuous Maven"
            url = uri("https://maven.fzzyhmstrs.me/")
        }
    }

    dependencies {
        compileOnly("org.jetbrains.kotlin:kotlin-stdlib:${"kotlin_version"()}")
        compileOnly("org.jetbrains.kotlin:kotlin-reflect:${"kotlin_version"()}")

        implementation("org.jetbrains.kotlin:kotlin-reflect:${"kotlin_version"()}")
        implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:${"kotlinx_coroutines_version"()}")
        implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:${"kotlinx_serialization_version"()}")
    }

    tasks.withType<JavaCompile> {
        options.encoding = "UTF-8"
        options.release.set("jvm_target"().toInt())
    }

    kotlin {
        jvmToolchain("jvm_target"().toInt())
    }

    java {
        withSourcesJar()
    }

    tasks.processResources {
        val props = rootProject.properties.mapValues { it.value.toString() }

        inputs.properties(props)

        filesMatching(processedFiles) {
            expand(props)
        }
    }

    val buildNumber = System.getenv("GITHUB_RUN_NUMBER")?.let { "-build.$it" } ?: ""

    tasks.register<Jar>("prodJar") {
        from(tasks.named("compileKotlin"))
        from(sourceSets.main.get().resources)

        if (project.name == rootProject.name) {
            archiveBaseName.set(archiveBaseName.get())
        } else {
            archiveBaseName.set(archiveBaseName.get() + "-" + project.name)
        }

        archiveClassifier.set(null as String?)
        archiveVersion.set("${version}+mc${rootProject.property("minecraft_version")}$buildNumber")
    }

    tasks.register<DokkaTask>("dokkaAll") {
        dokkaSourceSets {
            subprojects.map {
                create(it.name) {
                    sourceRoots.from(it.sourceSets.main.get().kotlin)
                    includeNonPublic.set(true)

                    externalDocumentationLink {
                        url.set(URI("https://kotlinlang.org/api/kotlinx.serialization/").toURL())
                    }

                    externalDocumentationLink {
                        url.set(URI("https://kotlinlang.org/api/kotlinx.coroutines/").toURL())
                    }

                    externalDocumentationLink {
                        url.set(URI("https://maven.stardustmodding.org/dokka/releases/net/fabricmc/yarn/1.20.1+build.local/raw/").toURL())
                        packageListUrl.set(URI("${url.get()}yarn/package-list").toURL())
                    }

                    externalDocumentationLink {
                        url.set(URI("https://maven.stardustmodding.org/dokka/releases/net/fabricmc/fabric-api/fabric-api/0.92.2+local-1.20.1/raw/").toURL())
                        packageListUrl.set(URI("${url.get()}fabric-api/package-list").toURL())
                    }
                }
            }
        }

        failOnWarning.set(false)
        outputDirectory.set(file("${layout.buildDirectory.get()}/docs/dokka"))
    }

    tasks.register<Jar>("dokkaJar") {
        dependsOn(tasks.named("dokkaAll"))
        from(tasks.named<DokkaTask>("dokkaAll").get().outputDirectory)

        if (project.name == rootProject.name) {
            archiveBaseName.set(archiveBaseName.get())
        } else {
            archiveBaseName.set(archiveBaseName.get() + "-" + project.name)
        }

        archiveClassifier.set("dokka")
        archiveVersion.set("${version}+mc${rootProject.property("minecraft_version")}$buildNumber")
    }

    tasks.kotlinSourcesJar {
        from(subprojects.map {
            it.sourceSets.main.get().allSource
        })

        duplicatesStrategy = DuplicatesStrategy.INCLUDE

        if (project.name == rootProject.name) {
            archiveBaseName.set(archiveBaseName.get())
        } else {
            archiveBaseName.set(archiveBaseName.get() + "-" + project.name)
        }

        archiveClassifier.set("sources")
        archiveVersion.set("${version}+mc${rootProject.property("minecraft_version")}$buildNumber")
    }

    tasks.shadowJar.get().finalizedBy(
        tasks.named("prodJar"),
        tasks.named("dokkaJar"),
        tasks.kotlinSourcesJar,
    )

    configure<PublishingExtension> {
        publications {
            create<MavenPublication>("mod") {
                groupId = "org.stardustmodding.interstellar"
                version = rootProject.property("mod_version")!! as String
                pom.packaging = "jar"

                if (project.name == rootProject.name) {
                    artifactId = "interstellar"
                } else {
                    artifactId = "interstellar-${project.name}"
                }

                if (tasks.names.contains("remapJar")) {
                    artifact(tasks.named("remapJar"))
                } else {
                    artifact(tasks.named("shadowJar"))
                }

                artifact(tasks.named("prodJar"))
                artifact(tasks.named("dokkaJar"))
                artifact(tasks.kotlinSourcesJar)
            }
        }

        repositories {
            if (System.getenv("MAVEN_USER") != null && System.getenv("MAVEN_PASSWORD") != null) {
                maven {
                    name = "StardustModding"
                    url = uri("https://maven.stardustmodding.org/snapshots/")

                    credentials {
                        username = System.getenv("MAVEN_USER")
                        password = System.getenv("MAVEN_PASSWORD")
                    }
                }
            }

            mavenLocal()
        }
    }
}

subprojects {
    apply(plugin = "dev.architectury.loom")

    val loom = project.extensions.getByName<LoomGradleExtensionAPI>("loom")

    loom.silentMojangMappingsLicense()

    repositories {
        mavenLocal()
    }

    dependencies {
        "minecraft"("com.mojang:minecraft:${rootProject.property("minecraft_version")}")

        @Suppress("UnstableApiUsage") "mappings"(loom.layered {
            officialMojangMappings()
            parchment(
                "org.parchmentmc.data:parchment-${rootProject.property("minecraft_version_minor")}:${
                    rootProject.property(
                        "parchment_version"
                    )
                }@zip"
            )
        })
    }

    loom.apply {
        runs {
            named("client") {
                vmArg("-Dmixin.debug.export=true")
            }

            named("server") {
                vmArg("-Dmixin.debug.export=true")
            }
        }
    }
}

operator fun String.invoke(): String {
    return rootProject.ext[this] as? String ?: throw IllegalStateException("Property $this is not defined")
}
