package org.stardustmodding.skyengine.net.packet

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import dev.architectury.networking.NetworkManager
import net.fabricmc.api.EnvType
import net.minecraft.core.BlockPos
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.level.ChunkPos
import net.minecraft.world.level.block.state.BlockState
import org.stardustmodding.interstellar.api.net.CodecPacket
import org.stardustmodding.interstellar.api.util.codecs.ExtraCodecs
import org.stardustmodding.skyengine.SkyEngine
import java.util.*

class ShipUpdatedS2CPacket(
    private val uuid: UUID, private val chunkPos: ChunkPos, private val blocks: Map<BlockPos, BlockState>
) : CodecPacket<ShipUpdatedS2CPacket>() {
    override val id: ResourceLocation = SkyEngine.id("s2c/ship_updated")
    override val side: NetworkManager.Side = NetworkManager.Side.S2C

    override val codec: Codec<ShipUpdatedS2CPacket> = RecordCodecBuilder.create { it ->
        it.group(ExtraCodecs.UUID_CODEC.fieldOf("uuid").forGetter { it.uuid },
            ExtraCodecs.CHUNK_POS.fieldOf("chunkPos").forGetter { it.chunkPos },
            Codec.unboundedMap(ExtraCodecs.BLOCK_POS_KEY, BlockState.CODEC).fieldOf("blocks").forGetter { it.blocks })
            .apply(it, ::ShipUpdatedS2CPacket)
    }

    override fun apply(ctx: NetworkManager.PacketContext) {
        if (ctx.env != EnvType.CLIENT) return

        SkyEngine.SHIPS[uuid]?.chunkPos = chunkPos
        SkyEngine.SHIPS[uuid]?.setBlocks(HashMap(blocks))
    }

    companion object {
        internal fun createForInit() = ShipUpdatedS2CPacket(UUID.randomUUID(), ChunkPos(0, 0), mutableMapOf())
    }
}
