import net.fabricmc.loom.task.RemapJarTask
import net.fabricmc.loom.build.nesting.NestableJarGenerationTask

tasks.register<RemapJarTask>("remapJar") {
//    nestedJars.setFrom()
}

tasks.register<NestableJarGenerationTask>("processIncludeJars")

architectury {
    common("enabled_platforms"().split(","))
}

loom {
    accessWidenerPath.set(file("src/main/resources/interstellar.accesswidener"))
}

dependencies {
    modImplementation("net.fabricmc:fabric-loader:${"fabric_loader_version"()}")

//    modApi("foundry.veil:Veil-mojmap-${rootProject.property("minecraft_version")}:${rootProject.property("veil_version")}")
    modImplementation("com.tterrag.registrate_fabric:Registrate:${"registrate_fabric_version"()}")
    modImplementation("me.fzzyhmstrs:fzzy_config:${"fzzy_config"()}+${"minecraft_version_minor"()}")
    modImplementation("dev.architectury:architectury:${"architectury_version"()}")
}

operator fun String.invoke(): String {
    return rootProject.ext[this] as? String
        ?: throw IllegalStateException("Property $this is not defined")
}
