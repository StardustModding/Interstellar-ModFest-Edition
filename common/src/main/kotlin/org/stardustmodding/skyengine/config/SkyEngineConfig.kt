package org.stardustmodding.skyengine.config

import me.fzzyhmstrs.fzzy_config.annotations.ClientModifiable
import me.fzzyhmstrs.fzzy_config.config.ConfigSection
import me.fzzyhmstrs.fzzy_config.validation.number.ValidatedInt

class SkyEngineConfig : ConfigSection() {
    @ClientModifiable
    var plotyardDistance = ValidatedInt(8192, Int.MAX_VALUE, 1024)

    @ClientModifiable
    var plotSpacing = ValidatedInt(16, Int.MAX_VALUE, 4)
}
