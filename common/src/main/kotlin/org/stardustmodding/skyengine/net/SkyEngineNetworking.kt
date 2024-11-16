@file:Suppress("removal", "DEPRECATION")

package org.stardustmodding.skyengine.net

import dev.architectury.networking.NetworkChannel
import net.minecraft.server.level.ServerPlayer
import org.stardustmodding.skyengine.SkyEngine
import org.stardustmodding.skyengine.net.packet.ShipKilledS2CPacket
import org.stardustmodding.skyengine.net.packet.ShipUpdatedS2CPacket

object SkyEngineNetworking {
    private val channel = NetworkChannel.create(SkyEngine.id("networking"))
    private var registered = false // its a bugfix but still smh

    fun init() {
        if (registered) return

        channel.register(
            ShipUpdatedS2CPacket::class.java,
            ShipUpdatedS2CPacket::encode,
            { ShipUpdatedS2CPacket.createForInit().decode(it) },
            ShipUpdatedS2CPacket::applySupplier
        )

        channel.register(
            ShipKilledS2CPacket::class.java,
            ShipKilledS2CPacket::encode,
            { ShipKilledS2CPacket.createForInit().decode(it) },
            ShipKilledS2CPacket::applySupplier
        )

        registered = true
    }

    fun <T> sendToPlayer(player: ServerPlayer, message: T) {
        channel.sendToPlayer(player, message)
    }

    fun <T> sendToServer(message: T) {
        channel.sendToServer(message)
    }

    fun <T> sendToPlayers(players: Iterable<ServerPlayer>, message: T) {
        channel.sendToPlayers(players, message)
    }
}
