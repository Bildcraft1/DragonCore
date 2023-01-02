package fur.kiyoshi.dragoncore.api

import fur.kiyoshi.dragoncore.commands.otherplugins.BloodMoonStatus
import fur.kiyoshi.dragoncore.commands.tags.Tags.tags
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

        if (DragonAPI().getConfig().getBoolean("functions.tag_system")) {
            if (identifier.equals("tag", ignoreCase = true)) {
                if (player != null) {
                    return if (player.hasPermission("dragoncore.tag")) {
                        // Kinda of a hacky way to do this, but it works
                        if (PlaceholderAPI.setPlaceholders(player,
                                DragonAPI().getConfig().getString("tags.topplayer.placeholder")!!
                            ) == player.displayName) {
                            DragonAPI().getConfig().getString("tags.topplayer.tag").toString()
                        } else if (tags.contains(player) && tags[player] == "Staff") {
                            DragonAPI().getConfig().getString("tags.staff.tag").toString()
                        } else if(player.hasPermission("dragoncore.vip")) {
                            ""
                        } else {
                            ""
                        }
                    } else {
                        ""
                    }
                }
            }

            if (identifier.equals("tagtab", ignoreCase = true)) {
                if (player != null) {
                    return if (player.hasPermission("dragoncore.tag")) {
                        // Kinda of a hacky way to do this, but it works
                        if (PlaceholderAPI.setPlaceholders(player,
                                DragonAPI().getConfig().getString("tags.topplayer.placeholder")!!
                            ) == player.displayName) {
                            DragonAPI().getConfig().getString("tags.topplayer.tab_tag").toString()
                        } else if (tags.contains(player) && tags[player] == "Staff") {
                            DragonAPI().getConfig().getString("tags.staff.tab_tag").toString()
                        } else if(player.hasPermission("dragoncore.vip")) {
                            ""
                        } else {
                            ""
                        }
                    } else {
                        ""
                    }
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