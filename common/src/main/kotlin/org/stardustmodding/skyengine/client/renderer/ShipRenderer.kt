package org.stardustmodding.skyengine.client.renderer

import com.mojang.blaze3d.vertex.PoseStack
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.RenderType
import net.minecraft.client.renderer.entity.EntityRenderer
import net.minecraft.client.renderer.entity.EntityRendererProvider
import net.minecraft.client.renderer.texture.OverlayTexture
import net.minecraft.resources.ResourceLocation
import org.stardustmodding.interstellar.impl.Interstellar
import org.stardustmodding.skyengine.client.entity.ShipModel
import org.stardustmodding.skyengine.entity.ShipEntity

@Environment(EnvType.CLIENT)
class ShipRenderer(ctx: EntityRendererProvider.Context) : EntityRenderer<ShipEntity>(ctx) {
    private val model = ShipModel()

    override fun getTextureLocation(entity: ShipEntity) = textureId()

    override fun render(
        entity: ShipEntity,
        yaw: Float,
        tickDelta: Float,
        stack: PoseStack,
        vertexConsumers: MultiBufferSource,
        light: Int
    ) {
        val layer = RenderType.solid()
        val consumer = vertexConsumers.getBuffer(layer)

        model.entity = entity

        stack.pushPose()
        model.renderToBuffer(stack, consumer, light, OverlayTexture.NO_OVERLAY, 0)
        stack.popPose()

        super.render(entity, yaw, tickDelta, stack, vertexConsumers, light)
    }

    companion object {
        fun textureId(): ResourceLocation {
            return Interstellar.id("ship")
        }
    }
}
