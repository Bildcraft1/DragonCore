package fur.kiyoshi.dragoncore.api

import org.apache.http.client.methods.CloseableHttpResponse
import org.apache.http.client.methods.HttpPost
import org.apache.http.entity.StringEntity
import org.apache.http.impl.client.CloseableHttpClient
import org.apache.http.impl.client.HttpClients
import org.bukkit.entity.Player


class ReportAPI {
    fun createReport(player: Player?, reason: String, reporter: Player) {
        val db = DragonDatabase()
        db.getConnection()
        val statement = db.conn?.createStatement()
        statement?.executeUpdate("INSERT INTO `dragoncore_reports` (uuid, name, reason, reporter) VALUES ('${player?.uniqueId}', '${player?.name}', '${reason}', '${reporter.name}');")
        db.closeConnection()
    }

    fun deleteReport(player: Player) {
        val db = DragonDatabase()
        db.getConnection()
        val statement = db.conn?.createStatement()
        statement?.executeUpdate("DELETE FROM `dragoncore_reports` WHERE uuid = '${player.uniqueId}';")
        db.closeConnection()
    }

    fun getReports(): List<Report> {
        val db = DragonDatabase()
        db.getConnection()
        val statement = db.conn?.createStatement()
        val result = statement?.executeQuery("SELECT * FROM `dragoncore_reports`;")
        val reports = mutableListOf<Report>()
        while (result?.next()!!) {
            reports.add(Report(result.getString("uuid"), result.getString("name"), result.getString("reason"), result.getString("reporter")))
        }
        db.closeConnection()
        return reports
    }

    fun sendDiscordReport(player: Player, reason: String, reporter: Player) {
        val httpclient: CloseableHttpClient = HttpClients.createDefault()
        val httpPost = HttpPost(DragonAPI().getConfig().getString("discord.webhook"))
        reporter.sendMessage(httpPost.uri.toString())

        val json = "{\"username\": \"DragonCore\", \"embeds\": [{\"title\": \"New Report\", \"description\": \"${reporter.name} has reported ${player.name} for ${reason}\", \"color\": 16711680}]}"
        val entity = StringEntity(json)
        httpPost.entity = entity
        httpPost.setHeader("Accept", "application/json");
        httpPost.setHeader("Content-type", "application/json");

        val response: CloseableHttpResponse = httpclient.execute(httpPost)
        assert(response.statusLine.statusCode == 204)
        if (response.statusLine.statusCode == 204) {
            reporter.sendMessage("Report sent to discord")
        } else {
            reporter.sendMessage("Report failed to send to discord" + response.statusLine.statusCode)
            reporter.sendMessage(response.toString())
        }
        entity.content?.use { }
        httpclient.close()
    }

    class Report {
        val uuid: String
        val name: String
        val reason: String
        val reporter: String

        constructor(uuid: String, name: String, reason: String, reporter: String) {
            this.uuid = uuid
            this.name = name
            this.reason = reason
            this.reporter = reporter
        }
    }
}