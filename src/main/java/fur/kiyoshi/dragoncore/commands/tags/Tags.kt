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

    private const val sql = "SELECT tags FROM dragoncore WHERE uuid = ?"
    private val conn: Connection = DragonDatabase().getConnection()
    private var statement: PreparedStatement? = conn.prepareStatement(sql)
    // Load the tags of the player
    fun loadTags(player: Player) {
        statement?.setString(1, player.uniqueId.toString())
        val result = statement?.executeQuery()
        if (result?.next()!!) {
            tags[player] = result.getString("tags")
        }
    }

    fun resetTags(player: Player) {
        val sql = "UPDATE dragoncore SET tags = ? WHERE uuid = ?"
        val conn: Connection = DragonDatabase().getConnection()
        val statement: PreparedStatement? = conn.prepareStatement(sql)
        statement?.setString(1, "None")
        statement?.setString(2, player.uniqueId.toString())
        statement?.executeUpdate()
        tags[player] = "None"
    }

    var inv: Inventory? = null

    // Load tags at boot
    fun loadAllTags() {
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

        if (args.size == 1) {
            if(args[0] == "clear") {
                tags[sender] = "None"
                resetTags(sender)
                sender.sendMessage(color("&aTags cleared!"))
                return true
            }
        }

        if(args.size == 2) {
            if(args[0] == "remove") {
                if(sender.hasPermission("dragoncore.tags.remove")) {
                    val target = Bukkit.getPlayer(args[1])
                    if(target == null) {
                        sender.sendMessage(color("&cPlayer not found"))
                        return true
                    }
                    tags[target] = "None"
                    resetTags(target)
                    sender.sendMessage(color("&aTags cleared!"))
                    return true
                }
            }
        }

        if (sender.hasPermission("dragoncore.tagsmanage")) {
            if (!tags.containsKey(sender)) {
                tags[sender] = "None"
            }

            var inventorysize = DragonAPI().getConfig().getConfigurationSection("tags")!!.getKeys(false).size * 9

            if (inventorysize > 54) {
                inventorysize = 54
            }

            // Create a new inventory, with no owner (as this isn't a real inventory), a size of nine, called example
            inv = Bukkit.createInventory(null, inventorysize, color("&b&lTags"))


            // Create a item for every item inside the configuration get the name and the material and the lore
            for (item in DragonAPI().getConfig().getConfigurationSection("tags")!!.getKeys(false)) {
                val itemStack = DragonAPI().getConfig().getItemStack("tags.$item.item")
                val itemMeta = itemStack?.itemMeta
                itemMeta?.setDisplayName(color(DragonAPI().getConfig().getString("tags.$item.name")))
                if (itemMeta != null) {
                    itemMeta.lore = DragonAPI().getConfig().getStringList("tags.$item.tag")
                }
                if (itemStack != null) {
                    itemStack.itemMeta = itemMeta
                }
                if (sender.hasPermission("dragoncore.tags.${DragonAPI().getConfig().getString("tags.$item.name")}")) {
                    inv!!.addItem(DragonAPI().createGuiItem(DragonAPI().getConfig().getString("tags.$item.item")?.let { Material.getMaterial(it) }, color(DragonAPI().getConfig().getString("tags.$item.name")),
                            "Preview of the tag: " + color(DragonAPI().getConfig().getString("tags.$item.tag")),
                            if (tags[sender] == item) "§aCurrently Equipped" else "§cNot Equipped"))
                    blockInt++
                }
            }

            for (x in blockInt until inv!!.size - 1) {
                inv!!.addItem(
                    DragonAPI().createGuiItemCustom(
                        Material.BLACK_STAINED_GLASS_PANE,
                        "",
                        "",
                    )
                )
                x + 1
            }


            inv!!.addItem(
                DragonAPI().createGuiItem(
                    Material.BARRIER,
                    "§cRemove Tag",
                    "§aRemove all tags",
                )
            )


            sender.openInventory(inv!!)
            return true
        }

        return true
    }

}