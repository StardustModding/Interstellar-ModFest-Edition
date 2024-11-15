package org.stardustmodding.interstellar.api.platform

import dev.architectury.injectables.annotations.ExpectPlatform

object PlatformHelper {
    @JvmStatic
    @ExpectPlatform
    fun createEntityId(id: String): String? {
        throw AssertionError()
    }

    @JvmStatic
    @ExpectPlatform
    fun finalizeRegistrate() {
        throw AssertionError()
    }
}
