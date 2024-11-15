package org.stardustmodding.interstellar.api.platform.neoforge

import org.stardustmodding.interstellar.impl.Interstellar

object PlatformHelperImpl {
    @JvmStatic
    fun createEntityId(id: String): String = Interstellar.id(id).toString()

    @JvmStatic
    fun finalizeRegistrate() {}
}
