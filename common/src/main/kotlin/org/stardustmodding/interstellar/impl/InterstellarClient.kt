package org.stardustmodding.interstellar.impl

import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import org.stardustmodding.skyengine.SkyEngineClient

@Environment(EnvType.CLIENT)
object InterstellarClient {
    fun init() {
        SkyEngineClient.init()
    }
}
