package org.stardustmodding.interstellar.api.platform.forge

import org.stardustmodding.interstellar.impl.Interstellar

object PlatformHelperImpl {
    @JvmStatic
    fun createEntityId(id: String): String = Interstellar.id(id).toString()
}
