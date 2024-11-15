package org.stardustmodding.skyengine.ext

import net.minecraft.world.level.ChunkPos

object ChunkPosExt {
    fun ChunkPos.offset(x: Int, z: Int) = ChunkPos(this.x + x, this.z + z)
}
