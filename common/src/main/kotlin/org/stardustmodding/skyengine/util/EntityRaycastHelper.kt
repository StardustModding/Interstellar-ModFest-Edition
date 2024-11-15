package org.stardustmodding.skyengine.util

import net.minecraft.core.Direction
import net.minecraft.world.entity.Entity
import net.minecraft.world.level.Level
import net.minecraft.world.phys.AABB
import net.minecraft.world.phys.EntityHitResult
import net.minecraft.world.phys.Vec3
import java.util.function.Predicate

object EntityRaycastHelper {
    @JvmStatic
    fun getEntityHitResult(
        level: Level,
        dir: Direction,
        source: Entity,
        startVec: Vec3,
        endVec: Vec3,
        boundingBox: AABB,
        filter: Predicate<Entity?>,
        inflationAmount: Float
    ): EntityHitResult? {
        var maxDistance = Double.MAX_VALUE
        var entity: Entity? = null
//        var pos: Vec3? = null
        val entities = level.getEntities(source, boundingBox, filter).iterator()

        while (entities.hasNext()) {
            val currentEntity = entities.next()
            val aabb = currentEntity.boundingBox.inflate(inflationAmount.toDouble())

            if (aabb.intersects(startVec, endVec)) {
//                val hitPos = ????
                val distance = startVec.distanceToSqr(currentEntity.position())

                if (distance < maxDistance) {
                    entity = currentEntity
                    maxDistance = distance
//                    pos = hitPos
                }
            }
        }

        return if (entity == null) {
            null
        } else {
            EntityHitResult(entity, /*pos ?: */entity.position())
        }
    }
}
