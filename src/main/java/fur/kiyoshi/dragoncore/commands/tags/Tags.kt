package fur.kiyoshi.dragoncore.commands.tags

import fur.kiyoshi.dragoncore.api.DragonAPI
import fur.kiyoshi.dragoncore.api.DragonDatabase
import fur.kiyoshi.dragoncore.format.Format.color
import me.clip.placeholderapi.PlaceholderAPI
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory
import java.sql.Connection
import java.sql.PreparedStatement

object Tags: CommandExecutor {
    // Load from database the tags of the player and load them inside the map
    val tags = mutableMapOf<Player, String>()

    val sql = "SELECT tags FROM dragoncore WHERE name = ?"
    val conn: Connection = DragonDatabase().getConnection()
    var statement: PreparedStatement? = conn.prepareStatement(sql)
    // Load the tags of the player
    fun loadTags(player: Player) {
        statement?.setString(1, player.name)
        val result = statement?.executeQuery()
        if (result?.next()!!) {
            tags[player] = result.getString("tags")
        }
    }
    var inv: Inventory? = null

    // Load tags at boot
    fun loadTags() {
        for (player in Bukkit.getOnlinePlayers()) {
            loadTags(player)
        }
    }

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        var blockInt = 0

        if (sender !is Player) {
            sender.sendMessage("Your not a player")
            return true
        }
        loadTags(sender)

        if (sender.hasPermission("dragoncore.tagsmanage")) {
            if (!tags.containsKey(sender)) {
                tags[sender] = "None"
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
                        if (tags[sender] == "Staff") "§aCurrently Equipped" else "§cNot Equipped"
                    )
                )
                blockInt++
            }

            if (sender.hasPermission("dragoncore.vip")) {
                inv!!.addItem(
                    DragonAPI().createGuiItem(
                        Material.DIAMOND,
                        "§bVIP Tag",
                        "§aFirst line of the lore",
                        "§bSecond line of the lore",
                        if (tags[sender] == "VIP") "§aCurrently Equipped" else "§cNot Equipped"
                    )
                )
                blockInt++
            }

            if ((PlaceholderAPI.setPlaceholders(sender, DragonAPI().getConfig().getString("tags.topplayer.placeholder")!!) == sender.name) || sender.hasPermission("dragoncore.topplayer")) {
                inv!!.addItem(
                    DragonAPI().createGuiItem(
                        Material.NETHERITE_INGOT,
                        "§bTop Player Tag",
                        "§aFirst line of the lore",
                        "§bSecond line of the lore",
                        if (tags[sender] == "TopPlayer") "§aCurrently Equipped" else "§cNot Equipped"
                    )
                )
                blockInt++
            }

            for (x in blockInt until 8) {
                inv!!.addItem(
                    DragonAPI().createGuiItem(
                        Material.BLACK_STAINED_GLASS_PANE,
                        "",
                        x.toString(),
                    )
                )
                x + 1
            }


            inv!!.addItem(
                DragonAPI().createGuiItem(
                    Material.BARRIER,
                    "§cRemove Tag",
                    "§aFirst line of the lore",
                    "§bSecond line of the lore",
                    if (tags[sender] == "None") "§aCurrently Equipped" else "§cNot Equipped"
                )
            )


            sender.openInventory(inv!!)
            return true
        }

        return true
    }

}