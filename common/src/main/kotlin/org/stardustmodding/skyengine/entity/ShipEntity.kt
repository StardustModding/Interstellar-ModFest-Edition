package org.stardustmodding.skyengine.entity

import net.minecraft.core.BlockPos
import net.minecraft.core.DefaultedMappedRegistry
import net.minecraft.core.SectionPos
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.ListTag
import net.minecraft.nbt.NbtUtils
import net.minecraft.nbt.Tag
import net.minecraft.network.syncher.SynchedEntityData
import net.minecraft.util.Mth
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.EntityType
import net.minecraft.world.level.ChunkPos
import net.minecraft.world.level.ClipContext
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.chunk.LevelChunkSection
import net.minecraft.world.level.levelgen.structure.BoundingBox
import net.minecraft.world.phys.AABB
import net.minecraft.world.phys.Vec3
import org.joml.Quaternionf
import org.stardustmodding.interstellar.api.data.Tracked
import org.stardustmodding.skyengine.SkyEngine
import org.stardustmodding.skyengine.math.PosExt.toBlockPos
import org.stardustmodding.skyengine.plotyard.Plotyard
import org.stardustmodding.skyengine.plotyard.PlotyardManager

class ShipEntity(type: EntityType<*>, world: Level) : Entity(type, world) {
    private var registered = false
    var chunkPos: ChunkPos? = null

    var blocks: Tracked<HashMap<BlockPos, BlockState>> = Tracked(
        hashMapOf(
            BlockPos(-1, 0, 0) to Blocks.QUARTZ_BLOCK.defaultBlockState(),
            BlockPos(-1, 1, 0) to Blocks.QUARTZ_BLOCK.defaultBlockState(),
            BlockPos(-1, 2, 0) to Blocks.QUARTZ_BLOCK.defaultBlockState(),
            BlockPos(0, 0, 0) to Blocks.QUARTZ_BLOCK.defaultBlockState(),
            BlockPos(0, 1, 0) to Blocks.QUARTZ_BLOCK.defaultBlockState(),
            BlockPos(0, 2, 0) to Blocks.QUARTZ_BLOCK.defaultBlockState(),
            BlockPos(1, 0, 0) to Blocks.QUARTZ_BLOCK.defaultBlockState(),
            BlockPos(1, 1, 0) to Blocks.QUARTZ_BLOCK.defaultBlockState(),
            BlockPos(1, 2, 0) to Blocks.QUARTZ_BLOCK.defaultBlockState(),
        )
    )

    private fun bounds(): BoundingBox {
        if (blocks.get().keys.size == 0) return BoundingBox(0, 0, 0, 0, 0, 0)

        val min = blocks.get().keys.min()
        val max = blocks.get().keys.max()

        return BoundingBox.fromCorners(min, max)
    }

    fun aabb(): AABB = AABB.of(bounds()).move(position)

    private fun onBlocksUpdated() {
        if (!level.isClientSide) {
            PlotyardManager[level].updateShip(this)
        }
    }

    override fun tick() {
        if (!registered && level.isClientSide) {
            SkyEngine.SHIPS[uuid] = this
            registered = true
        }

        register()
        boundingBox = aabb()
    }

    fun register() {
        if (!registered && server != null && !level.isClientSide) {
            chunkPos = PlotyardManager[level].registerShip(this)
            blocks.subscribe(this::onBlocksUpdated)
            boundingBox = aabb()
            registered = true
            onBlocksUpdated()
        }
    }

    override fun defineSynchedData(builder: SynchedEntityData.Builder) {}

    override fun readAdditionalSaveData(nbt: CompoundTag) {
        val list = nbt.getList("blocks", Tag.TAG_COMPOUND.toInt()) ?: return
        val reg = BuiltInRegistries.BLOCK as DefaultedMappedRegistry<Block>

        reg.frozen = false // HACK, BAD, THIS IS TERRIBLE

        val lookup = reg.createRegistrationLookup()
        val map = hashMapOf<BlockPos, BlockState>()

        for (rawItem in list) {
            val item = rawItem as CompoundTag
            val pos = NbtUtils.readBlockPos(item, "pos").get()
            val state = NbtUtils.readBlockState(lookup, item.getCompound("state"))

            map[pos] = state
        }

        reg.frozen = true
        setBlocks(map)
    }

    fun setBlocks(map: HashMap<BlockPos, BlockState>) {
        if (blocks.get().isNotEmpty()) {
            if (map.isNotEmpty()) blocks.set(map)
        } else {
            blocks.set(map)
        }
    }

    override fun addAdditionalSaveData(nbt: CompoundTag) {
        val list = ListTag()

        for (item in blocks.get()) {
            val compound = CompoundTag()

            compound.put("pos", NbtUtils.writeBlockPos(item.key))
            compound.put("state", NbtUtils.writeBlockState(item.value))

            list.add(compound)
        }

        nbt.put("blocks", list)
    }

    override fun kill() {
        SkyEngine.SHIPS.remove(uuid)
        PlotyardManager[level].freeShip(this)

        super.kill()
    }

    private fun getChunkRadius(): Int {
        val bounds = aabb()

        return Mth.ceil(listOf(bounds.xsize, bounds.ysize, bounds.zsize).max() / Plotyard.CHUNK_SIZE)
    }

    fun chunkSections(): List<Pair<SectionPos, LevelChunkSection>> {
        val radius = getChunkRadius()
        val list = mutableListOf<Pair<SectionPos, LevelChunkSection>>()

        ChunkPos.rangeClosed(chunkPosition(), radius).forEach {
            val chunk = level().getChunk(it.x, it.z)

            for (y in -radius..radius) {
                val index = chunk.getSectionIndex(y * 16)

                list.add(SectionPos.of(it.x, y, it.z) to chunk.sections[index])
            }
        }

        return list
    }

    fun getBlockPosAtHit(pos: Vec3): BlockPos {
//        if (rotation == Quaternionf()) return pos.subtract(position).toBlockPos()

        val inverse = Quaternionf()

//        rotationVector.invert(inverse)

        val blockPos =
            pos.toVector3f().rotate(inverse).sub(position.x.toFloat(), position.y.toFloat(), position.z.toFloat())
                .toBlockPos()

        return blockPos
    }

    fun getBlockAtHitPos(pos: Vec3): BlockState? = blocks.get()[getBlockPosAtHit(pos)]
    fun getBlockAtPos(pos: BlockPos): BlockState? = blocks.get()[pos]
    fun getRealPos(relative: BlockPos) = Plotyard.getRealPos(relative, chunkPos ?: ChunkPos(0, 0))
    fun getRealPos(pos: Vec3) = getRealPos(pos.toBlockPos())

    fun raycastLocalPos(start: Vec3, end: Vec3, origin: ChunkPos): Vec3? {
        if (chunkPos == null) return null

        val chunkDist = ChunkPos(chunkPos!!.x - origin.x, chunkPos!!.z - origin.z)

        val chunkOffset = Vec3(
            (chunkDist.x * Plotyard.CHUNK_SIZE).toDouble(),
            position.y - start.y,
            (chunkDist.z * Plotyard.CHUNK_SIZE).toDouble()
        )

        val result = level.clip(
            ClipContext(
                start.add(chunkOffset), end.add(chunkOffset), ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, this
            )
        )

        val loc = result.location.subtract(chunkOffset)

        if (blocks.get().containsKey(
                loc.toBlockPos()
            )
        ) {
            return loc
        }

        return null
    }
}
