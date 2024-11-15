package org.stardustmodding.interstellar.api.data

import net.minecraft.core.HolderLookup
import net.minecraft.nbt.CompoundTag
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.level.saveddata.SavedData
import kotlin.reflect.full.primaryConstructor

abstract class SavedState : SavedData() {
    abstract fun read(tag: CompoundTag): SavedState
    abstract fun write(nbt: CompoundTag): CompoundTag
    abstract fun default(): SavedState
    abstract val id: ResourceLocation

    open val shouldWrite get() = true

    override fun save(nbt: CompoundTag, registries: HolderLookup.Provider): CompoundTag = write(nbt)

    companion object {
        inline fun <reified T> readStatic(tag: CompoundTag): T where T : SavedState {
            val temp = T::class.primaryConstructor!!.call()

            return temp.read(tag) as T
        }

        inline fun <reified T> defaultStatic(): T where T : SavedState {
            val temp = T::class.primaryConstructor!!.call()

            return temp.default() as T
        }

        inline fun <reified T> idStatic(): ResourceLocation where T : SavedState {
            val temp = T::class.primaryConstructor!!.call()

            return temp.id
        }

        @Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
        inline fun <reified T> factory() where T : SavedState =
            Factory({ defaultStatic<T>() }, { tag, _ -> readStatic<T>(tag) }, null)
    }
}
