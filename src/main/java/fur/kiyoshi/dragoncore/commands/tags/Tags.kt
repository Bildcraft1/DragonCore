package fur.kiyoshi.dragoncore.commands.tags

import fur.kiyoshi.dragoncore.api.DragonAPI
import fur.kiyoshi.dragoncore.format.Format.color
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory

object Tags: CommandExecutor {
    var inv: Inventory? = null

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (sender !is Player) {
            sender.sendMessage("Your not a player")
            return true
        }


        if (sender.hasPermission("dragoncore.tagsmanage")) {
            // Create a new inventory, with no owner (as this isn't a real inventory), a size of nine, called example
            inv = Bukkit.createInventory(null, 9, color("&b&lTags"))

            inv!!.addItem(
                DragonAPI().createGuiItem(
                    Material.DIAMOND_SWORD,
                    "Example Sword",
                    "§aFirst line of the lore",
                    "§bSecond line of the lore"
                )
            )
            inv!!.addItem(
                DragonAPI().createGuiItem(
                    Material.IRON_HELMET,
                    "§bExample Helmet",
                    "§aFirst line of the lore",
                    "§bSecond line of the lore"
                )
            )

            sender.openInventory(inv!!)
            return true
        }

        return true
    }

}