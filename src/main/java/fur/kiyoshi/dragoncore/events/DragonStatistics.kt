package fur.kiyoshi.dragoncore.events

import fur.kiyoshi.dragoncore.Main
import fur.kiyoshi.dragoncore.api.DragonAPI
import org.apache.http.HttpResponse
import org.apache.http.NameValuePair
import org.apache.http.client.entity.UrlEncodedFormEntity
import org.apache.http.client.methods.HttpPost
import org.apache.http.impl.client.CloseableHttpClient
import org.apache.http.impl.client.HttpClients
import org.apache.http.message.BasicNameValuePair
import org.bukkit.scheduler.BukkitRunnable


class DragonStatistics: BukkitRunnable() {
    override fun run() {
        val httpclient: CloseableHttpClient = HttpClients.createDefault()
        val httpPost = HttpPost(DragonAPI().getConfig().getString("statistics_api.url"))

// Request parameters and other properties.
        // Request parameters and other properties.
        val params: MutableList<NameValuePair> = ArrayList(2)
        params.add(BasicNameValuePair("players", Main.instance.server.onlinePlayers.size.toString()))
        httpPost.entity = UrlEncodedFormEntity(params, "UTF-8")

        httpclient.execute(httpPost)
        httpclient.close()
    }
}