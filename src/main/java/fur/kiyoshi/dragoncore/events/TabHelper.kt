package fur.kiyoshi.dragoncore.events

import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter


class TabHelper: TabCompleter {
    var mutableList : MutableList<String> = ArrayList()

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
        }
        return null
    }

}