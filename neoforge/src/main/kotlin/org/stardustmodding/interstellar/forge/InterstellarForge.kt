package org.stardustmodding.interstellar.forge

import net.neoforged.fml.common.Mod
import org.stardustmodding.interstellar.impl.Interstellar
import org.stardustmodding.interstellar.impl.InterstellarClient
import thedarkcolour.kotlinforforge.neoforge.forge.DIST
import thedarkcolour.kotlinforforge.neoforge.forge.MOD_BUS

@Suppress("unused")
@Mod(Interstellar.MOD_ID)
object InterstellarForge {
    init {
        MOD_BUS.register(this)
        Interstellar.init()

        if (DIST.isClient) {
            InterstellarClient.init()
        }
    }
}
