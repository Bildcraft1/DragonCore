package com.whixard.dragoncore.api

import com.whixard.dragoncore.Main
import org.apache.http.client.methods.CloseableHttpResponse
import org.apache.http.client.methods.HttpPost
import org.apache.http.entity.StringEntity
import org.apache.http.impl.client.CloseableHttpClient
import org.apache.http.impl.client.HttpClients
import org.bukkit.entity.Player
import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet


class ReportAPI {
    fun createReport(player: Player?, reason: String, reporter: Player, status: Boolean) {
        val sql = "INSERT INTO `dragoncore_reports` (uuid, name, reason, reporter, status) VALUES (?,?,?,?,?);"
        val conn: Connection = DragonDatabase().getConnection()
        var statement: PreparedStatement? = conn.prepareStatement(sql)
        if (player != null) {
            statement?.setString(1, player.uniqueId.toString())
        }
        if (player != null) {
            statement?.setString(2, player.name)
        }
        statement?.setString(3, reason)
        statement?.setString(4, reporter.name)
        statement?.setBoolean(5, status)
        statement?.executeUpdate()
        conn.close()
    }

    fun deleteReport(id: Int) {
        val db = DragonDatabase()
        db.getConnection()
        val statement = db.conn?.createStatement()
        statement?.executeUpdate("DELETE FROM `dragoncore_reports` WHERE id = ${id};")
        db.closeConnection()
    }

    fun checkReport(id: Int): Boolean {
        val db = DragonDatabase()
        db.getConnection()
        val statement = db.conn?.createStatement()
        val result: ResultSet = statement?.executeQuery("SELECT * FROM `dragoncore_reports` WHERE id = ${id};")!!
        if (result.next()) {
            return result.getBoolean("status")
        }
        return false
    }

    fun setReport(id: Int, status: Boolean) {
        val db = DragonDatabase()
        db.getConnection()
        val statement = db.conn?.createStatement()
        statement?.executeUpdate("UPDATE `dragoncore_reports` SET status = $status WHERE id = $id;")
        db.closeConnection()
    }

    fun getReports(): List<Report> {
        val db = DragonDatabase()
        db.getConnection()
        val statement = db.conn?.createStatement()
        val result = statement?.executeQuery("SELECT * FROM `dragoncore_reports`;")
        val reports = mutableListOf<Report>()
        while (result?.next()!!) {
            reports.add(Report(result.getString("uuid"), result.getString("name"), result.getString("reason"), result.getString("reporter"), result.getInt("id"), result.getBoolean("status")))
        }
        db.closeConnection()
        return reports
    }

    fun getReport(id: Int): Report {
        val db = DragonDatabase()
        db.getConnection()
        val statement = db.conn?.createStatement()
        val result = statement?.executeQuery("SELECT * FROM `dragoncore_reports` WHERE id = '$id';")
        while (result?.next()!!) {
            return Report(result.getString("uuid"), result.getString("name"), result.getString("reason"), result.getString("reporter"), result.getInt("id"), result.getBoolean("status"))
        }
        db.closeConnection()
        return Report("null", "null", "null", "null", 0, false)
    }

    fun sendDiscordReport(player: Player, reason: String, reporter: Player) {
        val httpclient: CloseableHttpClient = HttpClients.createDefault()
        val httpPost = HttpPost(DragonAPI().getConfig().getString("discord.webhook"))

        val json = com.whixard.dragoncore.Main.instance.getResource("DiscordRequest.json")?.bufferedReader().use { it!!.readText() }
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
            reporter.sendMessage("§7Report failed to send to discord " + response.statusLine.statusCode)
        }
        entity.content?.use { }
        httpclient.close()
    }

    class Report {
        val status: String
        var id: Int = 0
        val uuid: String
        val name: String
        val reason: String
        val reporter: String

        constructor(uuid: String, name: String, reason: String, reporter: String, id: Int?, status: Boolean) {
            if (id != null) {
                this.id = id
            }
            this.status = status.toString()
            this.uuid = uuid
            this.name = name
            this.reason = reason
            this.reporter = reporter
        }
    }
}