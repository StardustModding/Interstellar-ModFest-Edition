architectury {
    platformSetupLoomIde()
    neoForge()
}

loom {
    accessWidenerPath.set(project(":common").loom.accessWidenerPath)
}

val common: Configuration by configurations.creating
val shadowCommon: Configuration by configurations.creating
val developmentNeoForge: Configuration by configurations.getting

configurations {
    compileOnly.configure { extendsFrom(common) }
    runtimeOnly.configure { extendsFrom(common) }
    developmentNeoForge.extendsFrom(common)
}

repositories {
    maven {
        name = "Kotlin for Forge"
        url = uri("https://thedarkcolour.github.io/KotlinForForge/")
    }
}

dependencies {
    neoForge("net.neoforged:neoforge:${"neoforge_version"()}")

    common(project(":common", "namedElements")) { isTransitive = false }
    shadowCommon(project(":common", "transformProductionNeoForge")) { isTransitive = false }

    modApi("dev.architectury:architectury-neoforge:${"architectury_version"()}")
//    modImplementation("foundry.veil:Veil-forge-${rootProject.property("minecraft_version")}:${rootProject.property("veil_version")}")

    modRuntimeOnly("maven.modrinth:yeetus-experimentus:${"yeetus_version"()}")

    modApi("thedarkcolour:kotlinforforge-neoforge:${"kotlin_for_forge_version"()}")

    modImplementation(include("com.tterrag.registrate:Registrate:${"registrate_forge_version"()}")!!)
    modImplementation("me.fzzyhmstrs:fzzy_config:${"fzzy_config"()}+${"minecraft_version_minor"()}+neoforge")
}

val buildNumber = System.getenv("GITHUB_RUN_NUMBER")?.let { "-build.$it" } ?: ""

tasks.shadowJar {
    exclude("fabric.mod.json")
    exclude("architectury.common.json")
    configurations = listOf(shadowCommon)
    archiveBaseName.set(archiveBaseName.get() + "-neoforge")
    archiveClassifier.set("dev-shadow")
    archiveVersion.set("${version}+mc${rootProject.property("minecraft_version")}$buildNumber")
}

tasks.remapJar {
    injectAccessWidener.set(true)
    inputFile.set(tasks.shadowJar.get().archiveFile)
    dependsOn(tasks.shadowJar)
    archiveBaseName.set(archiveBaseName.get() + "-neoforge")
    archiveClassifier.set(null as String?)
    archiveVersion.set("${version}+mc${rootProject.property("minecraft_version")}$buildNumber")
}

tasks.jar {
    archiveBaseName.set(archiveBaseName.get() + "-neoforge")
    archiveClassifier.set("dev")
    archiveVersion.set("${version}+mc${rootProject.property("minecraft_version")}$buildNumber")
}

tasks.sourcesJar {
    val commonSources = project(":common").tasks.getByName<Jar>("sourcesJar")
    dependsOn(commonSources)
    from(commonSources.archiveFile.map { zipTree(it) })
}

components.getByName("java") {
    this as AdhocComponentWithVariants
    this.withVariantsFromConfiguration(project.configurations["shadowRuntimeElements"]) {
        skip()
    }
}

operator fun String.invoke(): String {
    return rootProject.ext[this] as? String
        ?: throw IllegalStateException("Property $this is not defined")
}