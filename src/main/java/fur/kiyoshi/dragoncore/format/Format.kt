package fur.kiyoshi.dragoncore.format

import com.google.common.base.Preconditions
import net.md_5.bungee.api.ChatColor
import org.bukkit.Bukkit
import java.awt.Color
import java.util.regex.Matcher
import java.util.regex.Pattern

object Format {
    @JvmStatic
    fun color(s: String?): String {
        return ChatColor.translateAlternateColorCodes('&', s)
    }

    @JvmStatic
    fun rgb(red: Int?, green: Int?, blue: Int?, s: String?): String {
        return ChatColor.of(Color(red!!, green!!, blue!!)).toString() + color(s)
    }

    fun hex(message: String): String {
        @Suppress("NAME_SHADOWING") var message = message
        val pattern = Pattern.compile("#[a-fA-F0-9]{6}")
        var matcher = pattern.matcher(message)
        while (matcher.find()) {
            val hexCode = message.substring(matcher.start(), matcher.end())
            val replaceSharp = hexCode.replace('#', 'x')
            val ch = replaceSharp.toCharArray()
            val builder = StringBuilder("")
            for (c in ch) {
                builder.append("&$c")
            }
            message = message.replace(hexCode, builder.toString())
            matcher = pattern.matcher(message)
        }
        return ChatColor.translateAlternateColorCodes('&', message)
    }

    @Suppress("KotlinConstantConditions")
    private fun translateHex(msg: String): String {
        val hexColorPattern = Pattern.compile("#([A-Fa-f0-9]{6})")
        var useAllColors: Boolean? = null
        @Suppress("NAME_SHADOWING") var msg = msg
        Preconditions.checkNotNull(msg, "Cannot translate null text")
        msg = msg.replace("&g", "#2196F3")
            .replace("&h", "#2962FF")
        if (useAllColors == null) {
            val matcher: Matcher = Pattern.compile("[0-9]\\.[0-9]+").matcher(Bukkit.getBukkitVersion())
            if (matcher.find()) {
                try {
                    useAllColors = matcher.group(0).split("\\.")[1].toInt() >= 16
                } catch (ignored: NumberFormatException) {
                }
            }
        }
        if (!useAllColors!!) return msg
        val matcher: Matcher = hexColorPattern.matcher(msg)
        while (matcher.find()) {
            val match: String = matcher.group(0)
            msg = msg.replace(match, ChatColor.of(match).toString() + "")
        }
        return msg
    }

    fun getTranslated(msg: String?): String? {
        @Suppress("NAME_SHADOWING") var msg: String? = msg ?: return null
        msg = ChatColor.translateAlternateColorCodes('&', msg)
        msg = translateHex(msg)
        return msg
    }


}