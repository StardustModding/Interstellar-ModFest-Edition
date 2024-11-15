package org.stardustmodding.skyengine.plotyard

import net.minecraft.core.HolderLookup
import net.minecraft.core.registries.Registries
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.NbtOps
import net.minecraft.resources.ResourceKey
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.level.ChunkPos
import net.minecraft.world.level.Level
import net.minecraft.world.level.saveddata.SavedData
import org.stardustmodding.interstellar.api.util.codecs.CodecHelpers.decodeOrNull
import org.stardustmodding.interstellar.api.util.codecs.CodecHelpers.tryEncode
import org.stardustmodding.skyengine.SkyEngine

object PlotyardManager {
    private const val DATA_ID = "skyengine:plotyard_manager"
    private var managers: MutableMap<ResourceKey<Level>, Plotyard> = mutableMapOf()

    @JvmStatic
    fun isReserved(level: ResourceKey<Level>, pos: ChunkPos) = managers[level]?.offsets?.containsValue(pos) ?: false

    operator fun get(level: ResourceKey<Level>) = managers.getOrPut(level) { Plotyard(level) }
    operator fun get(level: Level) = this[level.dimension()]

    fun save(level: ServerLevel) {
        level.dataStorage.set(DATA_ID, PlotyardSerializer(managers))
    }

    @Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
    fun load(level: ServerLevel) {
        level.dataStorage.get(SavedData.Factory(::PlotyardSerializer, PlotyardSerializer::load, null), DATA_ID)
            ?.let { managers = it.managers }
    }

    private class PlotyardSerializer(val managers: MutableMap<ResourceKey<Level>, Plotyard>) : SavedData() {
        constructor() : this(mutableMapOf())

        override fun save(tag: CompoundTag, registries: HolderLookup.Provider): CompoundTag {
            for ((key, value) in managers) {
                val enc = Plotyard.CODEC.tryEncode(NbtOps.INSTANCE, value)

                if (enc == null) {
                    SkyEngine.LOGGER.error("Failed to serialize plotyard in dimension $key!")
                } else {
                    tag.put(key.toString(), enc)
                }
            }

            return tag
        }

        companion object {
            @Suppress("UNUSED_PARAMETER")
            fun load(tag: CompoundTag, registries: HolderLookup.Provider): PlotyardSerializer {
                val managers: MutableMap<ResourceKey<Level>, Plotyard> = mutableMapOf()

                for (key in tag.allKeys) {
                    val value = tag[key]
                    val dec = Plotyard.CODEC.decodeOrNull(NbtOps.INSTANCE, value)

                    if (dec == null) {
                        SkyEngine.LOGGER.error("Failed to deserialize plotyard from key $key!")
                    } else {
                        managers.put(ResourceKey.create(Registries.DIMENSION, ResourceLocation.parse(key)), dec)
                    }
                }

                return PlotyardSerializer(managers)
            }
        }
    }
}