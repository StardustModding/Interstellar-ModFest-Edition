package org.stardustmodding.skyengine.client.renderer

import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.blaze3d.vertex.VertexConsumer
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.util.Mth
import net.minecraft.world.level.ChunkPos
import net.minecraft.world.level.Level
import net.minecraft.world.phys.Vec3
import net.minecraft.world.phys.shapes.CollisionContext
import org.joml.Quaternionf
import org.stardustmodding.skyengine.entity.ShipEntity
import org.stardustmodding.skyengine.math.PosExt.toBlockPos

@Environment(EnvType.CLIENT)
object BlockOutlineRenderer {
    @JvmStatic
    fun renderHitOutline(
        startPos: Vec3,
        endPos: Vec3,
        level: Level,
        stack: PoseStack,
        cons: VertexConsumer,
        ship: ShipEntity,
        camX: Double,
        camY: Double,
        camZ: Double,
        origin: ChunkPos,
        pos: Vec3
    ) {
        val x = pos.x - camX
        val y = pos.y - camY
        val z = pos.z - camZ

//        val relPos = ship.getBlockPosAtHit(pos)
        val relPos = ship.raycastLocalPos(startPos, endPos, origin) ?: return
        val state = ship.getBlockAtPos(relPos.toBlockPos()) ?: return
        val shape = state.getShape(level, ship.getRealPos(relPos), CollisionContext.of(ship))

        stack.pushPose()

//        if (ship.rotation != Quaternionf()) {
//            stack.mulPose(ship.rotation)
//        }

        val pose = stack.last()

        shape.forAllEdges { minX, minY, minZ, maxX, maxY, maxZ ->
            var sizeX = (maxX - minX).toFloat()
            var sizeY = (maxY - minY).toFloat()
            var sizeZ = (maxZ - minZ).toFloat()
            val ratio = Mth.sqrt((sizeX * sizeX) + (sizeY * sizeY) + (sizeZ * sizeZ))

            sizeX /= ratio
            sizeY /= ratio
            sizeZ /= ratio

            cons.addVertex(pose, (minX + x).toFloat(), (minY + y).toFloat(), (minZ + z).toFloat()).setColor(
                0f, 0f, 0f, 0.4f
            ).setNormal(pose, sizeX, sizeY, sizeZ)

            cons.addVertex(pose, (maxX + x).toFloat(), (maxY + y).toFloat(), (maxZ + z).toFloat()).setColor(
                0f, 0f, 0f, 0.4f
            ).setNormal(pose, sizeX, sizeY, sizeZ)
        }

        stack.popPose()
    }
}
