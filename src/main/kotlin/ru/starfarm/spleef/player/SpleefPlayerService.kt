package ru.starfarm.spleef.player

import com.google.gson.reflect.TypeToken
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.scheduler.BukkitRunnable
import ru.starfarm.core.database.query.row.ValueQueryRow
import ru.starfarm.core.util.serializer.Serializer
import ru.starfarm.spleef.Database
import ru.starfarm.spleef.DatabaseConnection
import ru.starfarm.spleef.Plugin
import java.util.*
import java.util.concurrent.ConcurrentMap
import java.util.concurrent.TimeUnit


/**
 * @author nolleNNN
 * @Date 02.09.2023
 * @Time 20:57
 */
object SpleefPlayerService : BukkitRunnable() {
    private val Players = mutableMapOf<UUID, SpleefPlayerInfo>()

    private val items = object : TypeToken<ConcurrentMap<Int, Boolean>>() {}.type
    private val table = DatabaseConnection.getTable("spleef_players")

    init {
        runTaskTimerAsynchronously(Plugin, 1L, 20L * TimeUnit.MINUTES.toSeconds(5))
    }

    fun load(uuid: UUID) = load(Bukkit.getPlayer(uuid))

    private fun load(player: Player) {
        table.newDatabaseQuery()
            .selectQuery()
            .queryRow(ValueQueryRow("uuid", player.uniqueId))
            .executeQueryAsync(DatabaseConnection)
            .thenAccept {
                val spleefPlayerInfo: SpleefPlayerInfo
                if (!it.next()) {
                    table.newDatabaseQuery()
                        .insertQuery()
                        .queryRow(ValueQueryRow("rating", 0))
                        .queryRow(ValueQueryRow("wins", 0))
                        .queryRow(ValueQueryRow("draw", 0))
                        .queryRow(ValueQueryRow("lose", 0))
                        .queryRow(ValueQueryRow("coins", 0))
                        .queryRow(ValueQueryRow("items", mutableMapOf<Int, Boolean>()))
                        .executeAsync(DatabaseConnection)

                    spleefPlayerInfo = SpleefPlayerInfo(player.uniqueId, player, mutableMapOf())

                    Players[player.uniqueId] = spleefPlayerInfo
                    return@thenAccept
                }
                spleefPlayerInfo = SpleefPlayerInfo(
                    player.uniqueId,
                    player,
                    Serializer.fromJson(it.getString("items"), items),
                    it.getInt("rating"),
                    it.getInt("wins"),
                    it.getInt("draw"),
                    it.getInt("lose"),
                    it.getInt("coins")
                )

                Players[player.uniqueId] = spleefPlayerInfo
            }
    }

    fun unload(player: Player) = save(Players.remove(player.uniqueId)!!)

    private fun save(spleefPlayerInfo: SpleefPlayerInfo) {
        Database.executeUpdate(
            false,
            "UPDATE `${table.name}` SET `rating` = ?, `wins` = ?, `draw` = ?, `lose` = ?, `coins` = ?, `items` = ? WHERE `uuid` = ?",
            spleefPlayerInfo.rating,
            spleefPlayerInfo.wins,
            spleefPlayerInfo.draw,
            spleefPlayerInfo.lose,
            spleefPlayerInfo.coins,
            Serializer.toJson(spleefPlayerInfo.items),
            spleefPlayerInfo.uuid
        )
    }

    fun getSpleefPlayer(player: Player): SpleefPlayerInfo? = Players[player.uniqueId]
    fun getSpleefPlayer(uuid: UUID): SpleefPlayerInfo? = Players[uuid]

    override fun run() = Players.values.forEach { save(it) }

}