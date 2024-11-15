package org.stardustmodding.interstellar.impl.config

import me.fzzyhmstrs.fzzy_config.annotations.ClientModifiable
import me.fzzyhmstrs.fzzy_config.annotations.Version
import me.fzzyhmstrs.fzzy_config.config.Config
import org.stardustmodding.interstellar.impl.Interstellar
import org.stardustmodding.skyengine.config.SkyEngineConfig

@Version(1)
class ModConfig : Config(Interstellar.id("mod_config")) {
    @JvmField
    @ClientModifiable
    var enableDarkLoadingScreen = true

    @JvmField
    var skyEngine = SkyEngineConfig()
}
