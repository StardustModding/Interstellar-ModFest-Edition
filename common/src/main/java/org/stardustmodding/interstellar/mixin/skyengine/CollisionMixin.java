package org.stardustmodding.interstellar.mixin.skyengine;

import kotlin.Pair;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.stardustmodding.skyengine.collision.LevelCollisionHelper;

import java.util.List;

@Mixin(Entity.class)
public abstract class CollisionMixin {
    @Shadow
    public Level level;

    @Shadow
    public abstract AABB getBoundingBox();

    @Shadow
    public abstract Level level();

    @Inject(method = "collide", at = @At("HEAD"))
    public void customCollisions(Vec3 vec, CallbackInfoReturnable<Vec3> cir) {
        Entity me = (Entity) (Object) this;
        AABB bounds = getBoundingBox();

        if (LevelCollisionHelper.isCollidingWithShip(level(), me, bounds.expandTowards(vec))) {
            if (me instanceof Player) {
                List<Pair<BlockPos, VoxelShape>> colliders = LevelCollisionHelper.getShips(
                        level(),
                        me,
                        bounds.expandTowards(vec)
                );
            }
        }
    }

//    @Shadow
//    public abstract float maxUpStep();
//
//    @Shadow
//    public abstract boolean onGround();
//
//    @Shadow
//    private static List<VoxelShape> collectColliders(Entity entity, Level level, List<VoxelShape> list, AABB aABB) {
//        return null;
//    }
//
//    @Shadow
//    private static float[] collectCandidateStepUpHeights(AABB aABB, List<VoxelShape> list, float f, float g) {
//        return null;
//    }
//
//    @Shadow
//    private static Vec3 collideWithShapes(Vec3 vec, AABB aABB, List<VoxelShape> list) {
//        return null;
//    }

//    public Vec3 collide(Vec3 vec) {
//        AABB bounds = getBoundingBox();
//        List<VoxelShape> entityCollisions = level().getEntityCollisions((Entity) (Object) this, bounds.expandTowards(vec));
//        Vec3 collided = vec.lengthSqr() == 0.0 ? vec : Entity.collideBoundingBox((Entity) (Object) this, vec, bounds, level(), entityCollisions);
//
//        boolean didCollideX = vec.x != collided.x;
//        boolean didCollideY = vec.y != collided.y;
//        boolean didCollideZ = vec.z != collided.z;
//        boolean isBelow = didCollideY && vec.y < 0.0;
//
//        if (maxUpStep() > 0.0F && (isBelow || onGround()) && (didCollideX || didCollideZ)) {
//            AABB movedUp = isBelow ? bounds.move(0.0, collided.y, 0.0) : bounds;
//            AABB expandedUp = movedUp.expandTowards(vec.x, maxUpStep(), vec.z);
//
//            if (!isBelow) {
//                expandedUp = expandedUp.expandTowards(0.0, -9.999999747378752E-6, 0.0);
//            }
//
//            List<VoxelShape> colliders = collectColliders((Entity) (Object) this, level, entityCollisions, expandedUp);
//            float collidedY = (float) collided.y;
//            float[] stepUpHeights = collectCandidateStepUpHeights(movedUp, colliders, maxUpStep(), collidedY);
//
//            for (float stepUpHeight : stepUpHeights) {
//                Vec3 shapeCollided = collideWithShapes(new Vec3(vec.x, stepUpHeight, vec.z), movedUp, colliders);
//
//                if (shapeCollided.horizontalDistanceSqr() > shapeCollided.horizontalDistanceSqr()) {
//                    double movedY = bounds.minY - movedUp.minY;
//
//                    return shapeCollided.add(0.0, -movedY, 0.0);
//                }
//            }
//        }
//
//        return collided;
//    }
}
