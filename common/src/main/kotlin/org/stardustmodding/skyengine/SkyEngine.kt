package org.stardustmodding.skyengine

import dev.architectury.event.events.common.CommandRegistrationEvent
import net.minecraft.core.Registry
import net.minecraft.resources.ResourceKey
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.MinecraftServer
import org.slf4j.LoggerFactory
import org.stardustmodding.skyengine.command.PlotyardCommand
import org.stardustmodding.skyengine.entity.ShipEntity
import org.stardustmodding.skyengine.init.SkyEngineEntities
import org.stardustmodding.skyengine.net.SkyEngineNetworking
import org.stardustmodding.skyengine.plotyard.PlotyardManager
import java.util.*

object SkyEngine {
    const val MOD_ID = "skyengine"

    @JvmField
    val LOGGER = LoggerFactory.getLogger(MOD_ID)!!

    @JvmField
    val SHIPS = mutableMapOf<UUID, ShipEntity>()

    private var commands = listOf(
        PlotyardCommand()
    )

    fun init() {
        SkyEngineNetworking.init()
        SkyEngineEntities.ENTITY_TYPE_REGISTRY.register()

        CommandRegistrationEvent.EVENT.register { dispatcher, _, _ ->
            for (cmd in commands) {
                cmd.register(dispatcher)
            }
        }
    }

    fun initServer(server: MinecraftServer) {
        PlotyardManager.load(server.overworld())
    }

    fun save(server: MinecraftServer) {
        PlotyardManager.save(server.overworld())
    }

    fun id(value: String): ResourceLocation = ResourceLocation.fromNamespaceAndPath(MOD_ID, value)
    fun <T> rk(reg: ResourceKey<out Registry<T>>, value: String): ResourceKey<T> = ResourceKey.create(reg, id(value))
}
