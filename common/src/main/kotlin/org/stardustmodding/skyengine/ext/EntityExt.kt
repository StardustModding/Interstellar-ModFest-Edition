package org.stardustmodding.skyengine.ext

import net.minecraft.world.entity.Entity
import net.minecraft.world.level.chunk.LevelChunk
import net.minecraft.world.level.chunk.LevelChunkSection

object EntityExt {
    fun Entity.chunk(): LevelChunk = level.getChunkAt(blockPosition())

    fun Entity.chunkSection(): LevelChunkSection {
        val chunk = chunk()

        return chunk.getSection(chunk.getSectionIndex(blockPosition().y))
    }
}
