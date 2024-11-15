package org.stardustmodding.skyengine.accessor

import net.minecraft.core.Holder
import net.minecraft.world.level.biome.Biome
import net.minecraft.world.level.chunk.PalettedContainer

interface LevelChunkSectionAccessor {
    fun setBiomes(biomes: PalettedContainer<Holder<Biome>>)
}
