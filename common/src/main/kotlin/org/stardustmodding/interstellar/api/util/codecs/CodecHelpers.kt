package org.stardustmodding.interstellar.api.util.codecs

import com.mojang.serialization.Codec
import com.mojang.serialization.DynamicOps
import java.util.*

object CodecHelpers {
    fun <T> Codec<T>.mutableList(): Codec<MutableList<T>> {
        return this.listOf().xmap({ it.toMutableList() }, { it.toList() })
    }

    fun <T> Codec<T>.hashSet(): Codec<HashSet<T>> {
        return this.listOf().xmap({ it.toHashSet() }, { it.toList() })
    }

    fun <T, O> Codec<T>.decodeOrNull(ops: DynamicOps<O>, input: O): T? {
        return this.decode(ops, input).result().getOrNull()?.first
    }

    fun <T, O> Codec<T>.tryEncode(ops: DynamicOps<O>, input: T): O? {
        return this.encodeStart(ops, input).result().getOrNull()
    }

    private fun <T> Optional<T>.getOrNull(): T? {
        return this.orElse(null)
    }
}