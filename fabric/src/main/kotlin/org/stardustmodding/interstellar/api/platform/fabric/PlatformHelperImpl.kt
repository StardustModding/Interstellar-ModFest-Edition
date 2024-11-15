package org.stardustmodding.interstellar.api.platform.fabric

import org.stardustmodding.interstellar.impl.Interstellar

object PlatformHelperImpl {
    @JvmStatic
    fun createEntityId(id: String): String? = null

    @JvmStatic
    fun finalizeRegistrate() {
        Interstellar.registrate.register()
    }
}
