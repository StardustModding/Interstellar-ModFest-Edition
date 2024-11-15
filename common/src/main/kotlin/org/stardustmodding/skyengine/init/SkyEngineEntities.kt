package org.stardustmodding.skyengine.init

import net.minecraft.core.Registry
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.MobCategory
import org.stardustmodding.interstellar.api.platform.PlatformHelper
import org.stardustmodding.interstellar.impl.Interstellar
import org.stardustmodding.skyengine.entity.ShipEntity

@Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
object SkyEngineEntities {
    val SHIP: EntityType<ShipEntity> = Registry.register(
        BuiltInRegistries.ENTITY_TYPE, Interstellar.id("ship"), EntityType.Builder.of({ type, level ->
            ShipEntity(type, level)
        }, MobCategory.MISC).build(PlatformHelper.createEntityId("ship"))
    )
}
