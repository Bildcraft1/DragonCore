package com.whixard.dragoncore.commands.playercommands

import com.whixard.dragoncore.api.DragonDatabase
import net.md_5.bungee.api.chat.ClickEvent
import net.md_5.bungee.api.chat.TextComponent
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

object Friends: CommandExecutor {
    private val db = DragonDatabase()
    private fun addFriend(sender: Player, target: Player) {
        val conn = db.getConnection()
        val stmt = conn.prepareStatement("INSERT INTO `dragoncore_friends` (uuid, name, status, friend) VALUES (?,?,?,?);")
        stmt.setString(1, sender.uniqueId.toString())
        stmt.setString(2, sender.name)
        stmt.setString(3, 1.toString())
        stmt.setString(4, target.name)
        val result = stmt.executeQuery()
        if (result.next()) {
            sender.sendMessage("§cYou are already friends with this player!")
            return
        }
    }

    private fun checkFriend(sender: Player, target: Player): Boolean {
        val conn = db.getConnection()
        val stmt = conn.prepareStatement("SELECT * FROM `dragoncore_friends` WHERE uuid = ? AND friend = ?;")
        stmt.setString(1, sender.uniqueId.toString())
        stmt.setString(2, target.name)
        val result = stmt.executeQuery()
        if (result.next()) {
            return true
        }
        return false
    }

    private fun removeFriend(sender: CommandSender, target: Player) {
        val conn = db.getConnection()
        val stmt = conn.prepareStatement("DELETE FROM `dragoncore_friends` WHERE uuid = ? AND friend = ?;")
        stmt.setString(1, (sender as Player).uniqueId.toString())
        stmt.setString(2, target.name)
        stmt.executeUpdate()

        // Remove the friend from the other player's friend list
        val stmt2 = conn.prepareStatement("DELETE FROM `dragoncore_friends` WHERE uuid = ? AND friend = ?;")
        stmt2.setString(1, target.uniqueId.toString())
        stmt2.setString(2, sender.name)
        stmt2.executeUpdate()

        target.sendMessage("§c${sender.name} is no longer friends with you!")

        sender.sendMessage("§aYou are no longer friends with ${target.name}!")
    }

    private fun getFriends(sender: Player): List<String> {
        val conn = db.getConnection()
        val stmt = conn.prepareStatement("SELECT * FROM `dragoncore_friends` WHERE uuid = ?;")
        stmt.setString(1, sender.uniqueId.toString())
        val result = stmt.executeQuery()
        val friends = mutableListOf<String>()
        while (result.next()) {
            if (!result.getBoolean("status")) continue
            friends.add(result.getString("friend"))
        }
        return friends
    }

    private fun listFriends(sender: CommandSender) {
        sender.sendMessage("§aFriends:")
        for (friend in getFriends(sender as Player)) {
            sender.sendMessage("§a${friend}")
        }
    }

    private fun sendFriendRequest(sender: CommandSender, target: Player) {
        val conn = db.getConnection()
        val stmt = conn.prepareStatement("INSERT INTO `dragoncore_friends` (uuid, name, status, friend) VALUES (?,?,?,?);")
        stmt.setString(1, (sender as Player).uniqueId.toString())
        stmt.setString(2, sender.name)
        stmt.setString(3, 0.toString())
        stmt.setString(4, target.name)
        stmt.executeUpdate()

        // Update the other player's status
        val stmt2 = conn.prepareStatement("INSERT INTO `dragoncore_friends` (uuid, name, status, friend) VALUES (?,?,?,?);")
        stmt2.setString(1, target.uniqueId.toString())
        stmt2.setString(2, target.name)
        stmt2.setString(3, 0.toString())
        stmt2.setString(4, sender.name)
        stmt2.executeUpdate()

        sender.sendMessage("§aFriend request sent!")

        val targetPlayer = sender.server.getPlayer(target.uniqueId)
        targetPlayer?.sendMessage("§aYou have received a friend request from ${sender.name}!")
        val acceptButton = TextComponent("§a[Accept]")
        acceptButton.clickEvent = ClickEvent(ClickEvent.Action.RUN_COMMAND, "/friends accept ${sender.name}")

        val denyButton = TextComponent("§c[Deny]")
        denyButton.clickEvent = ClickEvent(ClickEvent.Action.RUN_COMMAND, "/friends deny ${sender.name}")

        targetPlayer?.spigot()?.sendMessage(acceptButton, denyButton)
    }


    private fun checkFriendRequest(sender: Player, target: Player): Boolean {
        val conn = db.getConnection()
        val stmt = conn.prepareStatement("SELECT * FROM `dragoncore_friends` WHERE uuid = ? AND friend = ?;")
        stmt.setString(1, target.uniqueId.toString())
        stmt.setString(2, sender.name)
        val result = stmt.executeQuery()
        if (result.next()) {
            return true
        }
        return false
    }

    private fun acceptFriendRequest(sender: Player, target: Player) {
        if (checkFriend(sender, target)) {
            sender.sendMessage("§cYou are already friends with this player!")
            return
        }

        val conn = db.getConnection()

        if (!checkFriendRequest(sender, target)) {
            sender.sendMessage("§cYou do not have a friend request from this player!")
            return
        }

        val stmt = conn.prepareStatement("UPDATE `dragoncore_friends` SET status = ? WHERE uuid = ? AND friend = ?;")
        stmt.setString(1, 1.toString())
        stmt.setString(2, sender.uniqueId.toString())
        stmt.setString(3, target.name)
        stmt.executeUpdate()
        // Update the other player's status
        val stmt2 = conn.prepareStatement("UPDATE `dragoncore_friends` SET status = ? WHERE uuid = ? AND friend = ?;")
        stmt2.setString(1, 1.toString())
        stmt2.setString(2, target.uniqueId.toString())
        stmt2.setString(3, sender.name)
        stmt2.executeUpdate()
        sender.sendMessage("§aYou are now friends with ${target.name}!")
    }

    private fun denyFriendRequest(sender: Player, target: Player) {
        val conn = db.getConnection()

        if (!checkFriendRequest(sender, target)) {
            sender.sendMessage("§cYou do not have a friend request from this player!")
            return
        }

        val stmt = conn.prepareStatement("DELETE FROM `dragoncore_friends` WHERE uuid = ? AND friend = ?;")
        stmt.setString(1, sender.uniqueId.toString())
        stmt.setString(2, target.name)
        stmt.executeUpdate()
        // Update the other player's status
        val stmt2 = conn.prepareStatement("DELETE FROM `dragoncore_friends` WHERE uuid = ? AND friend = ?;")
        stmt2.setString(1, target.uniqueId.toString())
        stmt2.setString(2, sender.name)
        stmt2.executeUpdate()
        sender.sendMessage("§aYou have denied the friend request from ${target.name}!")
    }

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (args.isEmpty()) {
            listFriends(sender)
            return true
        }

        if (args.size < 2) {
            sender.sendMessage("§cUsage: /friends <add|remove|list|request|accept|deny> <player>")
            return true
        }

        val target = sender.server.getPlayer(args[1])

        if (target == null) {
            sender.sendMessage("§cPlayer not found!")
            return true
        }

        when (args[0]) {
            "add" -> {
                if (!sender.hasPermission("dragoncore.friends.add")) {
                    sender.sendMessage("§cYou do not have permission to add friends!")
                    return true
                }
                if (args.size < 2) {
                    sender.sendMessage("§cUsage: /friends add <player>")
                    return true
                }
                if (checkFriend(sender as Player, target)) {
                    sender.sendMessage("§cYou are already friends with this player!")
                    return true
                }
                addFriend(sender, target)
            }
            "remove" -> {
                if (args.size < 2) {
                    sender.sendMessage("§cUsage: /friends remove <player>")
                    return true
                }
                removeFriend(sender, target)
            }
            "list" -> {
                listFriends(sender)
            }
            "request" -> {
                if (args.size < 2) {
                    sender.sendMessage("§cUsage: /friends request <player>")
                    return true
                }
                sendFriendRequest(sender, target)
            }
            "accept" -> {
                if (args.size < 2) {
                    sender.sendMessage("§cUsage: /friends accept <player>")
                    return true
                }
                acceptFriendRequest(sender as Player, target)
            }
            "deny" -> {
                if (args.size < 2) {
                    sender.sendMessage("§cUsage: /friends deny <player>")
                    return true
                }
                denyFriendRequest(sender as Player, target)
            }
            else -> {
                sender.sendMessage("§cUsage: /friends [add/remove/list/request]")
            }
        }

        return true
    }
}