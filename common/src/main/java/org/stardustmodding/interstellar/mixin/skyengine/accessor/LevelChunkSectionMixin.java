package org.stardustmodding.interstellar.mixin.skyengine.accessor;

import net.minecraft.core.Holder;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.chunk.PalettedContainer;
import net.minecraft.world.level.chunk.PalettedContainerRO;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.stardustmodding.skyengine.accessor.LevelChunkSectionAccessor;

@Mixin(LevelChunkSection.class)
public class LevelChunkSectionMixin implements LevelChunkSectionAccessor {
    @Shadow
    private PalettedContainerRO<Holder<Biome>> biomes;

    @Override
    public void setBiomes(@NotNull PalettedContainer<Holder<Biome>> biomes) {
        this.biomes = biomes;
    }
}
