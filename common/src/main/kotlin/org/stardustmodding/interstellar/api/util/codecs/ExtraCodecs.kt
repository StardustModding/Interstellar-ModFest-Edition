package org.stardustmodding.interstellar.api.util.codecs

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.core.BlockPos
import net.minecraft.world.level.ChunkPos
import java.util.*

object ExtraCodecs {
    val CHUNK_POS: Codec<ChunkPos> = RecordCodecBuilder.create { it ->
        it.group(Codec.INT.fieldOf("x").forGetter { it.x }, Codec.INT.fieldOf("z").forGetter { it.z })
            .apply(it, ::ChunkPos)
    }

    val UUID_CODEC: Codec<UUID> = Codec.STRING.xmap(UUID::fromString, UUID::toString)

    val BLOCK_POS_KEY: Codec<BlockPos> = Codec.STRING.xmap({
        val sp = it.split(",")
        BlockPos(sp[0].toInt(), sp[1].toInt(), sp[2].toInt())
    }, {
        "${it.x},${it.y},${it.z}"
    })
}