package org.stardustmodding.interstellar.mixin.skyengine;

import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ChunkMap;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.PalettedContainer;
import net.minecraft.world.level.chunk.storage.ChunkSerializer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.stardustmodding.skyengine.accessor.LevelChunkSectionAccessor;
import org.stardustmodding.skyengine.plotyard.PlotyardManager;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

// THANKS, RYAN <3
@Mixin(ChunkMap.class)
public abstract class ChunkMapMixin {
    @Shadow
    @Final
    ServerLevel level;

    @Inject(method = "readChunk", at = @At("HEAD"), cancellable = true)
    private void readChunk(ChunkPos pos, CallbackInfoReturnable<CompletableFuture<Optional<CompoundTag>>> cir) {
        CompletableFuture<Optional<CompoundTag>> tagFuture = ((ChunkMap) (Object) this).read(pos);

        cir.setReturnValue(tagFuture.thenApplyAsync((optional) -> {
            if (PlotyardManager.isReserved(level.dimension(), pos)) {
                if (optional.isEmpty()) {
                    LevelChunk chunk = new LevelChunk(this.level, pos);
                    Registry<Biome> biomeRegistry = this.level.registryAccess().registryOrThrow(Registries.BIOME);

                    for (int i = 0; i < chunk.getSectionsCount(); i++) {
                        PalettedContainer<Holder<Biome>> container =
                                new PalettedContainer<>(
                                        biomeRegistry.asHolderIdMap(),
                                        biomeRegistry.getHolderOrThrow(Biomes.PLAINS),
                                        PalettedContainer.Strategy.SECTION_BIOMES
                                );

                        LevelChunkSectionAccessor section = (LevelChunkSectionAccessor) chunk.getSection(i);

                        section.setBiomes(container);
                    }

                    return Optional.of(ChunkSerializer.write(this.level, chunk));
                }
            }

            return optional;
        }));
    }
}
