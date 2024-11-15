package org.stardustmodding.interstellar.api.registry

import com.tterrag.registrate.AbstractRegistrate

class InterstellarRegistrate(modid: String) : AbstractRegistrate<InterstellarRegistrate>(modid) {
    companion object {
        fun create(modid: String) = InterstellarRegistrate(modid)
    }
}
