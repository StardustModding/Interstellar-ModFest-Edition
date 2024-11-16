package org.stardustmodding.interstellar.mixin.skyengine.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.TickRateManager;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.joml.Matrix4fStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
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

    @SuppressWarnings("InvalidInjectorMethodSignature") // IntelliJ doesn't know about the captured vars
    @Inject(
            method = "renderLevel",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/renderer/LevelRenderer;checkPoseStack(Lcom/mojang/blaze3d/vertex/PoseStack;)V",
                    ordinal = 2,
                    shift = At.Shift.AFTER
            ),
            locals = LocalCapture.CAPTURE_FAILHARD
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
            TickRateManager tickRateManager,
            float partialTicks,
            ProfilerFiller profilerFiller,
            Vec3 camPos,
            double camX,
            double camY,
            double camZ,
            boolean hasCapturedFrustum,
            Frustum frustum,
            float renderDistance,
            boolean shouldHaveFog,
            Matrix4fStack modelViewStack,
            boolean glowingThing,
            PoseStack poseStack,
            MultiBufferSource.BufferSource bufferSource,
            ObjectIterator<Entity> entitiesToRender
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
