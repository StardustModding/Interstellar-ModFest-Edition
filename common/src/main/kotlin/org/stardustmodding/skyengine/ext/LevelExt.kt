package org.stardustmodding.skyengine.ext

import net.minecraft.server.level.ServerLevel
import net.minecraft.world.level.ChunkPos
import net.minecraft.world.level.Level
import net.minecraft.world.level.chunk.LevelChunk

object LevelExt {
    operator fun Level.get(pos: ChunkPos): LevelChunk = getChunk(pos.x, pos.z)
    operator fun ServerLevel.get(pos: ChunkPos): LevelChunk = getChunk(pos.x, pos.z)
}
