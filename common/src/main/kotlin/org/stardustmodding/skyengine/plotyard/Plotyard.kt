@file:Suppress("LoggingSimilarMessage")

package org.stardustmodding.skyengine.plotyard

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.core.BlockPos
import net.minecraft.core.registries.Registries
import net.minecraft.resources.ResourceKey
import net.minecraft.server.MinecraftServer
import net.minecraft.world.entity.Entity
import net.minecraft.world.level.ChunkPos
import net.minecraft.world.level.Level
import net.minecraft.world.level.LevelAccessor
import net.minecraft.world.level.block.Blocks
import org.stardustmodding.interstellar.api.util.codecs.CodecHelpers.hashSet
import org.stardustmodding.interstellar.api.util.codecs.ExtraCodecs
import org.stardustmodding.interstellar.impl.Interstellar
import org.stardustmodding.skyengine.SkyEngine
import org.stardustmodding.skyengine.entity.ShipEntity
import org.stardustmodding.skyengine.net.SkyEngineNetworking
import org.stardustmodding.skyengine.net.packet.ShipKilledS2CPacket
import org.stardustmodding.skyengine.net.packet.ShipUpdatedS2CPacket
import org.stardustmodding.skyengine.plotyard.ChunkHelper.clear
import java.util.*
import kotlin.collections.component1
import kotlin.collections.component2
import kotlin.collections.set

class Plotyard(private var levelKey: ResourceKey<Level>) {
    private var getters: MutableMap<UUID, PlotyardBlockGetter> = mutableMapOf()
    private var cache: MutableMap<UUID, Int> = mutableMapOf()
    internal var offsets: MutableMap<UUID, ChunkPos> = mutableMapOf()
    internal var claimedChunks: HashSet<ChunkPos> = hashSetOf()

    /**
     * The base offset in the X direction
     */
    internal var baseOffset = 0

    constructor(
        level: ResourceKey<Level>,
        baseOffset: Int,
        offsets: MutableMap<UUID, ChunkPos>,
        claimedChunks: HashSet<ChunkPos>
    ) : this(level) {
        this.baseOffset = baseOffset
        this.offsets = offsets
        this.getters = mutableMapOf()
        this.claimedChunks = claimedChunks
    }

    private fun findNewChunk(): ChunkPos {
        baseOffset += PLOT_SPACING

        return ChunkPos(PLOTYARD_DISTANCE + baseOffset - PLOT_SPACING, PLOTYARD_DISTANCE)
    }

    fun registerShip(ship: ShipEntity): ChunkPos? {
        val server = ship.server ?: return null
        val uuid = ship.uuid

        if (offsets.containsKey(uuid)) {
            SkyEngine.LOGGER.warn("Ship with UUID $uuid already registered!")
            updateShipInternal(offsets[uuid]!!, server, ship)
            return offsets[uuid]
        }

        val chunkPos = findNewChunk()

        offsets[uuid] = chunkPos

        if (!updateShipInternal(chunkPos, server, ship)) {
            SkyEngine.LOGGER.error("Failed to update ship with UUID $uuid")
            return null
        }

        return chunkPos
    }

    fun updateShip(ship: ShipEntity) {
        val server = ship.server ?: return
        val uuid = ship.uuid

        if (!offsets.containsKey(uuid)) {
            SkyEngine.LOGGER.warn("Ship with UUID $uuid was not registered!")
            return
        }

        val chunkPos = offsets[uuid]!!

        if (!updateShipInternal(chunkPos, server, ship)) {
            SkyEngine.LOGGER.error("Failed to update ship with UUID $uuid")
        }
    }

    private fun updateShipInternal(chunkPos: ChunkPos, server: MinecraftServer, ship: ShipEntity): Boolean {
        if (ship.uuid in cache && cache[ship.uuid] == ship.blocks.get().hashCode()) {
            return true
        }

        val level = server.getLevel(levelKey)

        if (level == null) {
            SkyEngine.LOGGER.error("Failed to get level handle for $levelKey")
            return false
        }

        if (!claimedChunks.contains(chunkPos)) {
            claimedChunks.add(chunkPos)
            level.getChunk(chunkPos.x, chunkPos.z).clear()
        }

        for (x in -PLOT_SPACING..PLOT_SPACING) {
            for (z in -PLOT_SPACING..PLOT_SPACING) {
                if (!claimedChunks.contains(ChunkPos(chunkPos.x + x, chunkPos.z + z))) {
                    claimedChunks.add(ChunkPos(chunkPos.x + x, chunkPos.z + z))
                    level.getChunk(chunkPos.x + x, chunkPos.z + z).clear()
                    level.setChunkForced(chunkPos.x + x, chunkPos.z + z, true)
                }
            }
        }

        level.save(null, false, true)

        for ((pos, block) in ship.blocks.get()) {
            val realPos = getRealPos(pos, chunkPos)

            level.setBlockAndUpdate(realPos, block)
        }

        SkyEngineNetworking.sendToPlayers(
            server.playerList.players, ShipUpdatedS2CPacket(
                ship.uuid, chunkPos, ship.blocks.get()
            )
        )

        cache[ship.uuid] = ship.blocks.get().hashCode()

        return true
    }

    fun freeShip(ship: ShipEntity) {
        val server = ship.server ?: return
        val uuid = ship.uuid

        if (!offsets.containsKey(uuid)) {
            SkyEngine.LOGGER.warn("Ship with UUID $uuid was not registered!")
            return
        }

        val chunkPos = offsets[uuid]!!
        val level = server.getLevel(levelKey)

        if (level == null) {
            SkyEngine.LOGGER.error("Failed to get level handle for $levelKey")
            return
        }

        for ((pos, _) in ship.blocks.get()) {
            val realPos = getRealPos(pos, chunkPos)

            level.setBlockAndUpdate(realPos, Blocks.AIR.defaultBlockState())
        }

        offsets.remove(uuid)
        claimedChunks.remove(chunkPos)

        ChunkPos.rangeClosed(chunkPos, PLOT_SPACING).forEach {
            claimedChunks.remove(it)
        }

        SkyEngineNetworking.sendToPlayers(server.playerList.players, ShipKilledS2CPacket(uuid))

        cache.remove(uuid)
    }

    fun isRegistered(uuid: UUID) = offsets.containsKey(uuid)
    fun isRegistered(entity: Entity) = offsets.containsKey(entity.uuid)

    fun getBlockGetter(level: LevelAccessor, uuid: UUID): PlotyardBlockGetter? {
        val chunk = offsets[uuid] ?: return null

        return getters.getOrPut(uuid) { PlotyardBlockGetter(level, chunk) }
    }

    companion object {
        const val CHUNK_SIZE = 16

        val PLOTYARD_DISTANCE
            get() = (Interstellar.config?.skyEngine
                ?: throw RuntimeException("Cannot load config!")).plotyardDistance.get()

        val PLOT_SPACING
            get() = (Interstellar.config?.skyEngine ?: throw RuntimeException("Cannot load config!")).plotSpacing.get()

        fun getRealPos(relative: BlockPos, chunk: ChunkPos) =
            BlockPos((chunk.x * CHUNK_SIZE) + relative.x, relative.y, (chunk.z * CHUNK_SIZE) + relative.z)

        val CODEC: Codec<Plotyard> = RecordCodecBuilder.create { it ->
            it.group(ResourceKey.codec(Registries.DIMENSION).fieldOf("level").forGetter { it.levelKey },
                Codec.INT.fieldOf("baseOffset").forGetter { it.baseOffset },
                Codec.unboundedMap(ExtraCodecs.UUID_CODEC, ExtraCodecs.CHUNK_POS).fieldOf("offsets")
                    .forGetter { it.offsets },
                ExtraCodecs.CHUNK_POS.hashSet().fieldOf("claimedChunks").forGetter { it.claimedChunks })
                .apply(it, ::Plotyard)
        }
    }
}

