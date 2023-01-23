@file:Suppress("FunctionName")

package com.whixard.dragoncore.api.manager

class DragonManager(var main: com.whixard.dragoncore.Main) {
    private var plugin: com.whixard.dragoncore.Main? = null

    fun DragonManager(plugin: com.whixard.dragoncore.Main?) {
        this.plugin = plugin
    }

    fun getBackEnd(): com.whixard.dragoncore.Main? {
        return plugin
    }
}