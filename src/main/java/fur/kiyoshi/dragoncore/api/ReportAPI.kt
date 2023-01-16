package fur.kiyoshi.dragoncore.api

import fur.kiyoshi.dragoncore.Main
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

    fun deleteReport(id: Int) {
        val db = DragonDatabase()
        db.getConnection()
        val statement = db.conn?.createStatement()
        statement?.executeUpdate("DELETE FROM `dragoncore_reports` WHERE id = ${id};")
        db.closeConnection()
    }

    fun setStatus(id: Int, status: Int) {
        val db = DragonDatabase()
        db.getConnection()
        val statement = db.conn?.createStatement()
        statement?.executeUpdate("UPDATE `dragoncore_reports` SET status = 1 WHERE id = ${id};")
        db.closeConnection()
    }

    fun getReports(): List<Report> {
        val db = DragonDatabase()
        db.getConnection()
        val statement = db.conn?.createStatement()
        val result = statement?.executeQuery("SELECT * FROM `dragoncore_reports`;")
        val reports = mutableListOf<Report>()
        while (result?.next()!!) {
            reports.add(Report(result.getString("uuid"), result.getString("name"), result.getString("reason"), result.getString("reporter"), result.getInt("id")))
        }
        db.closeConnection()
        return reports
    }

    fun getReport(id: Int): Report {
        val db = DragonDatabase()
        db.getConnection()
        val statement = db.conn?.createStatement()
        val result = statement?.executeQuery("SELECT * FROM `dragoncore_reports` WHERE id = '$id';")
        val report = Report(result?.getString("uuid").toString(),
            result?.getString("name").toString(),
            result?.getString("reason").toString(), result?.getString("reporter").toString(), result?.getInt("id"))
        db.closeConnection()
        return report
    }

    fun sendDiscordReport(player: Player, reason: String, reporter: Player) {
        val httpclient: CloseableHttpClient = HttpClients.createDefault()
        val httpPost = HttpPost(DragonAPI().getConfig().getString("discord.webhook"))
        reporter.sendMessage(httpPost.uri.toString())

        val json = Main.instance.getResource("DiscordRequest.json")?.bufferedReader().use { it!!.readText() }
            .replace("%player%", player.name)
            .replace("%reason%", reason)
            .replace("%reporter%", reporter.name)
        val entity = StringEntity(json)
        httpPost.entity = entity
        httpPost.setHeader("Accept", "application/json");
        httpPost.setHeader("Content-type", "application/json");

        val response: CloseableHttpResponse = httpclient.execute(httpPost)
        assert(response.statusLine.statusCode == 204)
        if (response.statusLine.statusCode == 204) {
            reporter.sendMessage("§7Report sent to discord")
        } else {
            reporter.sendMessage("§7Report failed to send to discord" + response.statusLine.statusCode)
        }
        entity.content?.use { }
        httpclient.close()
    }

    class Report {
        var id: Int = 0
        val uuid: String
        val name: String
        val reason: String
        val reporter: String

        constructor(uuid: String, name: String, reason: String, reporter: String, id: Int?) {
            if (id != null) {
                this.id = id
            }
            this.uuid = uuid
            this.name = name
            this.reason = reason
            this.reporter = reporter
        }
    }
}