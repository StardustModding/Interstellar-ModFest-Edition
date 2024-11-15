package org.stardustmodding.interstellar.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.LoadingOverlay;
import net.minecraft.util.FastColor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.stardustmodding.interstellar.impl.Interstellar;

import java.util.Objects;

@Environment(EnvType.CLIENT)
@Mixin(LoadingOverlay.class)
public class LaunchScreenMixin {
    @Inject(method = "registerTextures", at = @At("HEAD"), cancellable = true)
    private static void registerTextures(Minecraft minecraft, CallbackInfo ci) {
        if (Objects.requireNonNull(Interstellar.config).enableDarkLoadingScreen) {
            minecraft.getTextureManager().register(
                    Interstellar.id("textures/gui/loading_logo.png"),
                    new LoadingOverlay.LogoTexture()
            );
            ci.cancel();
        }
    }

    @Inject(at = @At("HEAD"), method = "render")
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        if (Objects.requireNonNull(Interstellar.config).enableDarkLoadingScreen) {
            LoadingOverlay.LOGO_BACKGROUND_COLOR = FastColor.ARGB32.color(255, 31, 33, 32);
        }
    }
}
