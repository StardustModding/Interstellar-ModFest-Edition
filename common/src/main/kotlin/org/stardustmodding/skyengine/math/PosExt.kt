package org.stardustmodding.skyengine.math

import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.core.Vec3i
import net.minecraft.world.phys.Vec3
import org.joml.Quaternionf
import org.joml.Vector3d
import org.joml.Vector3f

object PosExt {
    fun BlockPos.toVec() = Vec3i(x, y, z)
    fun BlockPos.toVec3d(): Vec3 = Vec3(x.toDouble(), y.toDouble(), z.toDouble())

    fun BlockPos.getRelative(dir: Direction): BlockPos {
        return when (dir) {
            Direction.UP -> offset(0, 1, 0)
            Direction.DOWN -> offset(0, -1, 0)
            Direction.NORTH -> offset(0, 0, -1)
            Direction.EAST -> offset(1, 0, 0)
            Direction.SOUTH -> offset(0, 0, 1)
            Direction.WEST -> offset(-1, 0, 0)
        }
    }

    fun Vec3.toBlockPos(): BlockPos = BlockPos(x.toInt(), y.toInt(), z.toInt())
    fun Vector3d.toBlockPos(): BlockPos = BlockPos(x.toInt(), y.toInt(), z.toInt())
    fun Vector3f.toBlockPos(): BlockPos = BlockPos(x.toInt(), y.toInt(), z.toInt())
    fun Vec3.rotate(quat: Quaternionf) = Vec3(quat.transform(Vector3f(x.toFloat(), y.toFloat(), z.toFloat())))

    @JvmStatic
    fun Vec3.toVec3i(): Vec3i = Vec3i(x.toInt(), y.toInt(), z.toInt())
}
