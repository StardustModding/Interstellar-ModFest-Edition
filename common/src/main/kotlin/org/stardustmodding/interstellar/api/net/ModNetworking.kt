@file:Suppress("removal", "DEPRECATION")

package org.stardustmodding.interstellar.api.net

import dev.architectury.networking.NetworkChannel
import net.minecraft.server.level.ServerPlayer
import org.stardustmodding.interstellar.impl.Interstellar

object ModNetworking {
    private val channel = NetworkChannel.create(Interstellar.id("networking"))

    fun init() {

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