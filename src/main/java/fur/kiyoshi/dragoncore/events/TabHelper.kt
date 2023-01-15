package fur.kiyoshi.dragoncore.events

import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter


class TabHelper: TabCompleter {
    private var mutableList : MutableList<String> = ArrayList()

    override fun onTabComplete(
        sender: CommandSender,
        command: Command,
        label: String,
        args: Array<out String>
    ): MutableList<String>? {
        if (args.size != 2) {
            if (label == "freeze" && args[0].isEmpty()) {
                mutableList.add("clear")
                mutableList.add("100")
                return mutableList
            } else if (label == "freeze" && !args[0].isEmpty()) {
                return null
            }

            if(label == "tags" && args[0].isEmpty()) {
                if(sender.hasPermission("dragoncore.tags.remove")) {
                    mutableList.add("remove")
                    mutableList.add("clear")
                    return mutableList
                }
                mutableList.add("clear")
                return mutableList
            } else if (label == "tags" && !args[0].isEmpty()) {
                return null
            }

        }
        return null
    }
}