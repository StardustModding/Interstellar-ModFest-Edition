package org.stardustmodding.interstellar.mixin.skyengine;

import com.mojang.authlib.GameProfile;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.player.LocalPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.stardustmodding.skyengine.entity.ShipEntity;

import java.util.List;

@Mixin(LocalPlayer.class)
public abstract class LocalPlayerMixin extends AbstractClientPlayer {
    public LocalPlayerMixin(ClientLevel clientLevel, GameProfile gameProfile) {
        super(clientLevel, gameProfile);
    }

//    @Shadow
//    public abstract boolean isShiftKeyDown();

//    @Shadow
//    protected abstract boolean isControlledCamera();
//
//    @Shadow
//    protected abstract void sendIsSprintingIfNeeded();
//
//    @Shadow
//    private boolean wasShiftKeyDown;
//
//    @Shadow
//    private boolean autoJumpEnabled;
//
//    @Shadow
//    private double xLast;
//
//    @Shadow
//    private double yLast1;
//
//    @Shadow
//    private double zLast;
//
//    @Shadow
//    private float yRotLast;
//
//    @Shadow
//    private float xRotLast;
//
//    @Shadow
//    private int positionReminder;
//
//    @Final
//    @Shadow
//    public ClientPacketListener connection;
//
//    @Shadow
//    private boolean lastOnGround;

//    @Final
//    @Shadow
//    protected Minecraft minecraft;

    @Inject(method = "sendPosition", at = @At("HEAD"), cancellable = true)
    public void customSendPosition(CallbackInfo ci) {
        List<ShipEntity> entities = level.getEntitiesOfClass(ShipEntity.class, getBoundingBox());

        if (!entities.isEmpty()) {
            for (ShipEntity entity : entities) {
                if (entity.aabb().contains(position)) {
                    // TODO: This is the original code, I want to replace it with custom code based on the native collision system
//                    sendIsSprintingIfNeeded();
//                    boolean isCrouched = isShiftKeyDown();
//
//                    if (isCrouched != wasShiftKeyDown) {
//                        ServerboundPlayerCommandPacket.Action action = isCrouched ? ServerboundPlayerCommandPacket.Action.PRESS_SHIFT_KEY : ServerboundPlayerCommandPacket.Action.RELEASE_SHIFT_KEY;
//                        connection.send(new ServerboundPlayerCommandPacket((LocalPlayer) (Object) this, action));
//                        wasShiftKeyDown = isCrouched;
//                    }
//
//                    if (isControlledCamera()) {
//                        double deltaX = getX() - xLast;
//                        double deltaY = getY() - yLast1;
//                        double deltaZ = getZ() - zLast;
//                        double deltaYRot = getYRot() - yRotLast;
//                        double deltaXRot = getXRot() - xRotLast;
//
//                        ++positionReminder;
//
//                        boolean hasMoved = Mth.lengthSquared(deltaX, deltaY, deltaZ) > Mth.square(0.0002) || positionReminder >= 20;
//                        boolean hasRotated = deltaYRot != 0.0 || deltaXRot != 0.0;
//
//                        if (isPassenger()) {
//                            Vec3 movement = getDeltaMovement();
//                            connection.send(new ServerboundMovePlayerPacket.PosRot(movement.x, -999.0, movement.z, getYRot(), getXRot(), onGround()));
//                            hasMoved = false;
//                        } else if (hasMoved && hasRotated) {
//                            connection.send(new ServerboundMovePlayerPacket.PosRot(getX(), getY(), getZ(), getYRot(), getXRot(), onGround()));
//                        } else if (hasMoved) {
//                            connection.send(new ServerboundMovePlayerPacket.Pos(getX(), getY(), getZ(), onGround()));
//                        } else if (hasRotated) {
//                            connection.send(new ServerboundMovePlayerPacket.Rot(getYRot(), getXRot(), onGround()));
//                        } else if (lastOnGround != onGround()) {
//                            connection.send(new ServerboundMovePlayerPacket.StatusOnly(onGround()));
//                        }
//
//                        if (hasMoved) {
//                            xLast = getX();
//                            yLast1 = getY();
//                            zLast = getZ();
//                            positionReminder = 0;
//                        }
//
//                        if (hasRotated) {
//                            yRotLast = getYRot();
//                            xRotLast = getXRot();
//                        }
//
//                        lastOnGround = onGround();
//                        autoJumpEnabled = minecraft.options.autoJump().get();
//                    }

                    ci.cancel();
                }
            }
        }
    }
}
