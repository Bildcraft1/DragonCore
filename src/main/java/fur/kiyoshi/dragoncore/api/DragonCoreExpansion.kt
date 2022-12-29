package fur.kiyoshi.dragoncore.api

import fur.kiyoshi.dragoncore.commands.otherplugins.BloodMoonStatus
import me.clip.placeholderapi.PlaceholderAPI
import me.clip.placeholderapi.expansion.PlaceholderExpansion
import org.bukkit.Bukkit

class DragonCoreExpansion: PlaceholderExpansion() {

    override fun onPlaceholderRequest(player: org.bukkit.entity.Player?, identifier: String): String? {
        if (identifier.equals("bloodmoon", ignoreCase = true)) {
            if (player != null) {
                return if (BloodMoonStatus.bloodMoon && player.world.name == "mondo2") {
                    "true"
                } else {
                    "false"
                }
            } else if (BloodMoonStatus.bloodMoon) {
                return "true"
            } else {
                return "false"
            }
        }
        if (identifier.equals("topplayer", ignoreCase = true) && Bukkit.getServer().pluginManager.isPluginEnabled("ajLeaderboards")) {
            if (player != null) {
                return if (PlaceholderAPI.setPlaceholders(player, "%ajlb_lb_cmi_user_playtime_days_1_alltime_name%") == player.displayName) {
                    DragonAPI().getConfig().getString("topplayer.string").toString()
                } else {
                    ""
                }
            }
        }
        return null
    }

    /**
     * The placeholder identifier of this expansion. May not contain %,
     * {} or _
     *
     * @return placeholder identifier that is associated with this expansion
     */
    override fun getIdentifier(): String {
        return "dragoncore"
    }

    /**
     * The author of this expansion
     *
     * @return name of the author for this expansion
     */
    override fun getAuthor(): String {
        return "MyNameIsKiyoshi"
    }

    /**
     * The version of this expansion
     *
     * @return current version of this expansion
     */
    override fun getVersion(): String {
        return "1.0.0"
    }

    override fun persist(): Boolean {
        return true
    }

    override fun canRegister(): Boolean {
        return true
    }


}