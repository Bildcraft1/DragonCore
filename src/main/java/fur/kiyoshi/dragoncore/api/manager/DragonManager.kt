@file:Suppress("FunctionName")

package fur.kiyoshi.dragoncore.api.manager

import fur.kiyoshi.dragoncore.Main

class DragonManager(var main: Main) {
    private var plugin: Main? = null

    fun DragonManager(plugin: Main?) {
        this.plugin = plugin
    }

    fun getBackEnd(): Main? {
        return plugin
    }
}