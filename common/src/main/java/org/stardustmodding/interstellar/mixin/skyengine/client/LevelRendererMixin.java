package org.stardustmodding.interstellar.mixin.skyengine.client;

import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.*;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.stardustmodding.skyengine.client.renderer.BlockOutlineRenderer;
import org.stardustmodding.skyengine.entity.ShipEntity;
import org.stardustmodding.skyengine.math.PosExt;
import org.stardustmodding.skyengine.util.EntityRaycastHelper;

@Mixin(LevelRenderer.class)
@Environment(EnvType.CLIENT)
public abstract class LevelRendererMixin {
    @Shadow
    @Nullable
    private ClientLevel level;

    @Shadow
    @Final
    private Minecraft minecraft;

    @Inject(
            method = "renderLevel", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/renderer/LevelRenderer;checkPoseStack(Lcom/mojang/blaze3d/vertex/PoseStack;)V",
            ordinal = 2,
            shift = At.Shift.AFTER
    )
    )
    public void renderCustomBlockHitbox(
            DeltaTracker deltaTracker,
            boolean renderBlockOutline,
            Camera camera,
            GameRenderer gameRenderer,
            LightTexture lightTexture,
            Matrix4f frustumMatrix,
            Matrix4f projectionMatrix,
            CallbackInfo ci,
            @Local ProfilerFiller profilerFiller,
            @Local PoseStack poseStack,
            @Local MultiBufferSource.BufferSource bufferSource,
            @Local(name = "d") double camX,
            @Local(name = "e") double camY,
            @Local(name = "g") double camZ
    ) {
        assert minecraft.player != null;
        assert level != null;

        Vec3 startPos = camera.getPosition();
        Vec3 endPos = camera.getPosition().add(new Vec3(camera.getLookVector().mul(10)));
        AABB aabb = AABB.of(BoundingBox.fromCorners(PosExt.toVec3i(startPos), PosExt.toVec3i(endPos)));

        EntityHitResult hitResult = EntityRaycastHelper.getEntityHitResult(
                level,
                camera.getEntity().getDirection(),
                minecraft.player,
                startPos,
                endPos,
                aabb,
                (it) -> it instanceof ShipEntity,
                (float) minecraft.player.blockInteractionRange()
        );

        if (renderBlockOutline && hitResult != null) {
            profilerFiller.popPush("shipBlockOutline");

            Entity entity = hitResult.getEntity();

            if (entity instanceof ShipEntity ship && this.level.getWorldBorder().isWithinBounds(entity.position())) {
                VertexConsumer cons = bufferSource.getBuffer(RenderType.lines());

                BlockOutlineRenderer.renderHitOutline(
                        startPos,
                        endPos,
                        level,
                        poseStack,
                        cons,
                        ship,
                        camX,
                        camY,
                        camZ,
                        minecraft.player.chunkPosition(),
                        hitResult.getLocation()
                );
            }
        }
    }
}
