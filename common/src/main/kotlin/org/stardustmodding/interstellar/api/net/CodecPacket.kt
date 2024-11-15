package org.stardustmodding.interstellar.api.net

import com.mojang.serialization.Codec
import dev.architectury.networking.NetworkManager
import dev.architectury.networking.NetworkManager.PacketContext
import net.minecraft.nbt.NbtOps
import net.minecraft.nbt.Tag
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.resources.ResourceLocation
import org.stardustmodding.interstellar.api.util.codecs.CodecHelpers.decodeOrNull
import org.stardustmodding.interstellar.api.util.codecs.CodecHelpers.tryEncode
import java.util.function.Supplier

abstract class CodecPacket<T> {
    abstract val id: ResourceLocation
    abstract val codec: Codec<T>
    abstract val side: NetworkManager.Side
    abstract fun apply(ctx: PacketContext)

    @Suppress("UNCHECKED_CAST")
    open fun encode(buf: FriendlyByteBuf) {
        buf.writeNbt(codec.tryEncode(NbtOps.INSTANCE, this as T))
    }

    open fun decode(buf: FriendlyByteBuf): T? = codec.decodeOrNull(NbtOps.INSTANCE, buf.readNbt() as Tag)

    open fun applySupplier(ctx: Supplier<PacketContext>) {
        apply(ctx.get())
    }
}
