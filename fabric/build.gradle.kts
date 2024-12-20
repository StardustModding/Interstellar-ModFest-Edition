architectury {
    platformSetupLoomIde()
    fabric()
}

loom {
    accessWidenerPath.set(project(":common").loom.accessWidenerPath)
}

val common: Configuration by configurations.creating {
    isCanBeResolved = true
    isCanBeConsumed = false
}

val shadowBundle: Configuration by configurations.creating {
    isCanBeResolved = true
    isCanBeConsumed = false
}

val developmentFabric: Configuration by configurations.getting

configurations {
    compileOnly.get().extendsFrom(common)
    runtimeOnly.get().extendsFrom(common)
    developmentFabric.extendsFrom(common)
}

dependencies {
    modImplementation("net.fabricmc:fabric-loader:${rootProject.property("fabric_loader_version")}")
    modImplementation("net.fabricmc.fabric-api:fabric-api:${rootProject.property("fabric_api_version")}")

    common(project(":common", "namedElements")) { isTransitive = false }
    shadowBundle(project(":common", "transformProductionFabric"))

    modImplementation("dev.architectury:architectury-fabric:${rootProject.property("architectury_version")}")
    modImplementation("com.terraformersmc:modmenu:${rootProject.property("modmenu_version")}")
//    modImplementation("foundry.veil:Veil-fabric-${rootProject.property("minecraft_version")}:${rootProject.property("veil_version")}")
    modImplementation("net.fabricmc:fabric-language-kotlin:${rootProject.property("fabric_kotlin_version")}")
    modImplementation("me.fzzyhmstrs:fzzy_config:${"fzzy_config"()}+${"minecraft_version_minor"()}")
    modImplementation(include("com.tterrag.registrate_fabric:Registrate:${"registrate_fabric_version"()}")!!)

    modRuntimeOnly("maven.modrinth:yeetus-experimentus:${"yeetus_version"()}")
    modRuntimeOnly("maven.modrinth:mixintrace:1.1.1+1.17")
}

val buildNumber = System.getenv("GITHUB_RUN_NUMBER")?.let { "-build.$it" } ?: ""

tasks.shadowJar {
    exclude("architectury.common.json")
    configurations = listOf(shadowBundle)
    archiveBaseName.set(archiveBaseName.get() + "-fabric")
    archiveClassifier.set("dev-shadow")
    archiveVersion.set("${version}+mc${rootProject.property("minecraft_version")}$buildNumber")
}

tasks.remapJar {
    injectAccessWidener.set(true)
    inputFile.set(tasks.shadowJar.get().archiveFile)
    dependsOn(tasks.shadowJar)
    archiveBaseName.set(archiveBaseName.get() + "-fabric")
    archiveClassifier.set(null as String?)
    archiveVersion.set("${version}+mc${rootProject.property("minecraft_version")}$buildNumber")
}

tasks.jar {
    archiveBaseName.set(archiveBaseName.get() + "-fabric")
    archiveClassifier.set("dev")
    archiveVersion.set("${version}+mc${rootProject.property("minecraft_version")}$buildNumber")
}

tasks.sourcesJar {
    val commonSources = project(":common").tasks.getByName<Jar>("sourcesJar")
    dependsOn(commonSources)
    from(commonSources.archiveFile.map { zipTree(it) })
}

//components.getByName("java") {
//    this as AdhocComponentWithVariants
//    this.withVariantsFromConfiguration(project.configurations["shadowRuntimeElements"]) {
//        skip()
//    }
//}

operator fun String.invoke(): String {
    return rootProject.ext[this] as? String ?: throw IllegalStateException("Property $this is not defined")
}
