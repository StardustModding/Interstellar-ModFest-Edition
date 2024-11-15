package org.stardustmodding.interstellar.fabric

import net.fabricmc.api.ModInitializer
import org.stardustmodding.interstellar.impl.Interstellar

object InterstellarFabric : ModInitializer {
    override fun onInitialize() {
        Interstellar.init()
    }
}