package org.stardustmodding.skyengine.client.init

import dev.architectury.registry.client.level.entity.EntityModelLayerRegistry
import dev.architectury.registry.client.level.entity.EntityRendererRegistry
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.client.model.geom.ModelLayerLocation
import org.stardustmodding.skyengine.client.entity.ShipModel
import org.stardustmodding.skyengine.client.renderer.ShipRenderer
import org.stardustmodding.skyengine.init.SkyEngineEntities

@Environment(EnvType.CLIENT)
object ClientEntities {
    private val MODEL_SHIP_LAYER = ModelLayerLocation(ShipRenderer.textureId(), "main")

    fun init() {
        EntityRendererRegistry.register({ SkyEngineEntities.SHIP.get() }) { ctx ->
            ShipRenderer(ctx)
        }

        EntityModelLayerRegistry.register(MODEL_SHIP_LAYER, ShipModel::getTexturedModelData)
    }
}
