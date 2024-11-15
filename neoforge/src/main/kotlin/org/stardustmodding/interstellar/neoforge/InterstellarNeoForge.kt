package org.stardustmodding.interstellar.neoforge

import net.neoforged.fml.common.Mod
import org.stardustmodding.interstellar.impl.Interstellar
import org.stardustmodding.interstellar.impl.InterstellarClient
import thedarkcolour.kotlinforforge.neoforge.forge.DIST

@Suppress("unused")
@Mod(Interstellar.MOD_ID)
object InterstellarNeoForge {
    init {
        Interstellar.init()

        if (DIST.isClient) {
            InterstellarClient.init()
        }
    }
}
