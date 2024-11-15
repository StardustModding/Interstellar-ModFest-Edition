package org.stardustmodding.skyengine.net.packet

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import dev.architectury.networking.NetworkManager
import net.fabricmc.api.EnvType
import net.minecraft.resources.ResourceLocation
import org.stardustmodding.interstellar.api.net.CodecPacket
import org.stardustmodding.interstellar.api.util.codecs.ExtraCodecs
import org.stardustmodding.skyengine.SkyEngine
import org.stardustmodding.skyengine.client.entity.ShipModel
import java.util.*

class ShipKilledS2CPacket(private val uuid: UUID) : CodecPacket<ShipKilledS2CPacket>() {
    override val id: ResourceLocation = SkyEngine.id("s2c/ship_killed")
    override val side: NetworkManager.Side = NetworkManager.Side.S2C

    override val codec: Codec<ShipKilledS2CPacket> = RecordCodecBuilder.create { it ->
        it.group(ExtraCodecs.UUID_CODEC.fieldOf("uuid").forGetter { it.uuid }).apply(it, ::ShipKilledS2CPacket)
    }

    override fun apply(ctx: NetworkManager.PacketContext) {
        if (ctx.env != EnvType.CLIENT) return

        ShipModel.removeEntity(uuid)
        SkyEngine.SHIPS.remove(uuid)
    }

    companion object {
        internal fun createForInit() = ShipKilledS2CPacket(UUID.randomUUID())
    }
}
