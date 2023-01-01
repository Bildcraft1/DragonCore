package fur.kiyoshi.dragoncore.commands.tags

import fur.kiyoshi.dragoncore.api.DragonAPI
import fur.kiyoshi.dragoncore.format.Format.color
import me.clip.placeholderapi.PlaceholderAPI
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory

object Tags: CommandExecutor {
    val userTags: MutableMap<Player, String> = HashMap<Player, String>()
    var inv: Inventory? = null

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (sender !is Player) {
            sender.sendMessage("Your not a player")
            return true
        }

        if (sender.hasPermission("dragoncore.tagsmanage")) {
            if (!userTags.containsKey(sender)) {
                userTags[sender] = "None"
            }


            // Create a new inventory, with no owner (as this isn't a real inventory), a size of nine, called example
            inv = Bukkit.createInventory(null, 9, color("&b&lTags"))

            if (sender.hasPermission("dragoncore.staff")) {
                inv!!.addItem(
                    DragonAPI().createGuiItem(
                        Material.DIAMOND_SWORD,
                        "§bStaff Tag",
                        "§aFirst line of the lore",
                        "§bSecond line of the lore",
                        if (userTags[sender] == "Staff") "§aCurrently Equipped" else "§cNot Equipped"
                    )
                )
            }

            if (sender.hasPermission("dragoncore.vip")) {
                inv!!.addItem(
                    DragonAPI().createGuiItem(
                        Material.DIAMOND,
                        "§bVIP Tag",
                        "§aFirst line of the lore",
                        "§bSecond line of the lore",
                        if (userTags[sender] == "VIP") "§aCurrently Equipped" else "§cNot Equipped"
                    )
                )
            }

            if (PlaceholderAPI.setPlaceholders(sender, DragonAPI().getConfig().getString("tags.topplayer.placeholder")!!) == sender.displayName) {
                inv!!.addItem(
                    DragonAPI().createGuiItem(
                        Material.DIAMOND,
                        "§bTop Player Tag",
                        "§aFirst line of the lore",
                        "§bSecond line of the lore",
                        if (userTags[sender] == "TopPlayer") "§aCurrently Equipped" else "§cNot Equipped"
                    )
                )
            }

            sender.openInventory(inv!!)
            return true
        }

        return true
    }

}