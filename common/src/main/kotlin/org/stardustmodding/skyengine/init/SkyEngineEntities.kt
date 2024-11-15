package org.stardustmodding.skyengine.init

import dev.architectury.registry.registries.DeferredRegister
import dev.architectury.registry.registries.RegistrySupplier
import net.minecraft.core.registries.Registries
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.MobCategory
import org.stardustmodding.interstellar.api.platform.PlatformHelper
import org.stardustmodding.interstellar.impl.Interstellar
import org.stardustmodding.skyengine.entity.ShipEntity

@Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
object SkyEngineEntities {
    val ENTITY_TYPE_REGISTRY: DeferredRegister<EntityType<*>> =
        DeferredRegister.create(Interstellar.MOD_ID, Registries.ENTITY_TYPE)

    val SHIP: RegistrySupplier<EntityType<ShipEntity>> = ENTITY_TYPE_REGISTRY.register(Interstellar.id("ship")) {
        EntityType.Builder.of({ type, level ->
            ShipEntity(type, level)
        }, MobCategory.MISC).build(PlatformHelper.createEntityId("ship"))
    }
}
