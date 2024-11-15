package org.stardustmodding.skyengine

import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import org.stardustmodding.skyengine.client.init.ClientEntities

@Environment(EnvType.CLIENT)
object SkyEngineClient {
    fun init() {
        ClientEntities.init()
    }
}
