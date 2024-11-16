package org.stardustmodding.interstellar.impl

import dev.architectury.event.events.common.CommandRegistrationEvent
import dev.architectury.event.events.common.LifecycleEvent
import me.fzzyhmstrs.fzzy_config.api.ConfigApi
import net.minecraft.resources.ResourceLocation
import org.slf4j.LoggerFactory
import org.stardustmodding.interstellar.api.net.ModNetworking
import org.stardustmodding.interstellar.api.platform.PlatformHelper
import org.stardustmodding.interstellar.api.registry.InterstellarRegistrate
import org.stardustmodding.interstellar.impl.command.DimensionTpCommand
import org.stardustmodding.interstellar.impl.config.ModConfig
import org.stardustmodding.skyengine.SkyEngine

object Interstellar {
    const val MOD_ID = "interstellar_modfest_edition"

    @JvmField
    val LOGGER = LoggerFactory.getLogger(MOD_ID)!!

    @JvmField
    val registrate = InterstellarRegistrate(MOD_ID)

    @JvmField
    var config: ModConfig? = null

    private var commands = listOf(
        DimensionTpCommand()
    )

    @JvmStatic
    fun id(id: String): ResourceLocation {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, id)
    }

    fun init() {
        config = ConfigApi.registerAndLoadConfig(::ModConfig)

        ModNetworking.init()
        SkyEngine.init()

        LifecycleEvent.SERVER_STARTING.register(SkyEngine::initServer)
        LifecycleEvent.SERVER_STOPPING.register(SkyEngine::save)

        CommandRegistrationEvent.EVENT.register { dispatcher, _, _ ->
            for (cmd in commands) {
                cmd.register(dispatcher)
            }
        }

        PlatformHelper.finalizeRegistrate()
    }
}
