package org.stardustmodding.skyengine.plotyard

import net.minecraft.core.registries.Registries
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.level.chunk.LevelChunk
import net.minecraft.world.level.chunk.LevelChunkSection

object ChunkHelper {
    fun LevelChunk.clear() {
        clearAllBlockEntities()
        unregisterTickContainerFromLevel(level as ServerLevel)

        heightmaps.clear()
        sections.fill(null)

        val registry = level.registryAccess().registryOrThrow(Registries.BIOME)

        for (i in sections.indices) {
            if (sections[i] != null) continue
            sections[i] = LevelChunkSection(registry)
        }

        isLightCorrect = false
        registerTickContainerInLevel(level as ServerLevel)
        unsaved = true
    }
}
