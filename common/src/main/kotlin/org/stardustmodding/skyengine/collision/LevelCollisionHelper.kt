package org.stardustmodding.skyengine.collision

import com.google.common.collect.ImmutableList
import net.minecraft.core.BlockPos
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.EntitySelector
import net.minecraft.world.level.Level
import net.minecraft.world.phys.AABB
import net.minecraft.world.phys.shapes.Shapes
import net.minecraft.world.phys.shapes.VoxelShape
import org.stardustmodding.skyengine.entity.ShipEntity
import java.util.function.Predicate

object LevelCollisionHelper {
    private const val COLLISION_PADDING = 0.0000001

    @JvmStatic
    fun isCollidingWithShip(level: Level, entity: Entity?, collisionBox: AABB): Boolean {
        if (collisionBox.size < COLLISION_PADDING) {
            return false
        } else {
            var predicate: Predicate<Entity>

            if (entity == null) {
                predicate = EntitySelector.CAN_BE_COLLIDED_WITH
            } else {
                predicate = EntitySelector.NO_SPECTATORS
                predicate = predicate.and { it.canCollideWith(entity) }
            }

            return level.getEntitiesOfClass(ShipEntity::class.java, collisionBox.inflate(COLLISION_PADDING), predicate)
                .isNotEmpty()
        }
    }

    @JvmStatic
    fun getShips(level: Level, entity: Entity?, collisionBox: AABB): List<Pair<BlockPos, VoxelShape>> {
        if (collisionBox.size < COLLISION_PADDING) {
            return listOf()
        } else {
            var predicate: Predicate<Entity>

            if (entity == null) {
                predicate = EntitySelector.CAN_BE_COLLIDED_WITH
            } else {
                predicate = EntitySelector.NO_SPECTATORS
                predicate = predicate.and { it.canCollideWith(entity) }
            }

            val list =
                level.getEntitiesOfClass(ShipEntity::class.java, collisionBox.inflate(COLLISION_PADDING), predicate)

            if (list.isEmpty()) {
                return listOf()
            } else {
                val builder = ImmutableList.builderWithExpectedSize<Pair<BlockPos, VoxelShape>>(list.size)
                val entities: Iterator<Entity> = list.iterator()

                while (entities.hasNext()) {
                    val it = entities.next()

                    builder.add(it.blockPosition() to Shapes.create(it.boundingBox))
                }

                return builder.build()
            }
        }
    }
}
