import net.fabricmc.loom.task.RemapJarTask
import net.fabricmc.loom.build.nesting.NestableJarGenerationTask

tasks.register<RemapJarTask>("remapJar") {
    nestedJars.setFrom()
}

tasks.register<NestableJarGenerationTask>("processIncludeJars")

architectury {
    common(rootProject.property("enabled_platforms").toString().split(","))
}

loom {
    accessWidenerPath.set(file("src/main/resources/interstellar.accesswidener"))
}

dependencies {
    modImplementation("net.fabricmc:fabric-loader:${rootProject.property("fabric_loader_version")}")

    compileOnly("com.google.code.gson:gson:2.10.1")
//    compileOnly("foundry.veil:Veil-mojmap-${rootProject.property("minecraft_version")}:${rootProject.property("veil_version")}")
    compileOnly("com.tterrag.registrate_fabric:Registrate:${"registrate_fabric_version"()}")
    modApi("me.fzzyhmstrs:fzzy_config:${"fzzy_config_fabric"()}")

    modApi("dev.architectury:architectury:${rootProject.property("architectury_version")}")
    modCompileOnly("lol.bai:badpackets:fabric-${rootProject.property("badpackets_version")}")
}

tasks.processResources {
    inputs.property("version", version)
}

operator fun String.invoke(): String {
    return rootProject.ext[this] as? String
        ?: throw IllegalStateException("Property $this is not defined")
}
