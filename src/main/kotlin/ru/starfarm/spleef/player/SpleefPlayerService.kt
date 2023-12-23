package ru.starfarm.spleef.player

import com.google.gson.reflect.TypeToken
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import ru.starfarm.core.ApiManager
import ru.starfarm.core.database.query.row.ValueQueryRow
import ru.starfarm.core.util.serializer.Serializer
import ru.starfarm.core.util.time.Time
import ru.starfarm.spleef.Database
import ru.starfarm.spleef.DatabaseConnection
import ru.starfarm.spleef.Task
import ru.starfarm.spleef.items.ItemService
import ru.starfarm.spleef.lobby.util.moveToLobby
import ru.starfarm.spleef.service.Service
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*
import java.util.concurrent.ConcurrentMap

class SpleefPlayerService : Service(), ISpleefPlayerService {
    private val Players = hashMapOf<UUID, SpleefPlayerInfo>()
    private val Items = object : TypeToken<ConcurrentMap<Int, Boolean>>() {}.type
    private val Table = DatabaseConnection.getTable("spleef_players")

    init {
        Task.everyAsync(5, (20 * Time.ofMinutes(5).seconds).toInt()) {
            saveAllPlayers()
        }
    }

    override fun load(uuid: UUID) {
        Table.newDatabaseQuery()
                .selectQuery()
                .queryRow(ValueQueryRow("uuid", uuid.toString()))
                .executeQueryAsync(DatabaseConnection)
                .thenAccept {
                    val spleefPlayerInfo: SpleefPlayerInfo
                    if (!it.next()) {
                        Table.newDatabaseQuery()
                                .insertQuery()
                                .queryRow(ValueQueryRow("uuid", uuid.toString()))
                                .queryRow(ValueQueryRow("rating", 0))
                                .queryRow(ValueQueryRow("wins", 0))
                                .queryRow(ValueQueryRow("draw", 0))
                                .queryRow(ValueQueryRow("lose", 0))
                                .queryRow(ValueQueryRow("coins", 0))
                                .queryRow(ValueQueryRow("items", ItemService.playerItems))
                                .executeAsync(DatabaseConnection)

                        spleefPlayerInfo = SpleefPlayerInfo(uuid, ItemService.playerItems)

                        Players[uuid] = spleefPlayerInfo
                        return@thenAccept
                    }
                    spleefPlayerInfo = SpleefPlayerInfo(
                            uuid,
                            Serializer.fromJson(it.getString("items"), Items),
                            Bukkit.getPlayer(uuid),
                            it.getInt("rating"),
                            it.getInt("wins"),
                            it.getInt("draw"),
                            it.getInt("lose"),
                            it.getInt("coins")
                    )
                    Players[uuid] = spleefPlayerInfo
                    it.close()
                }
    }

    override fun load(player: Player) {
        load(player.uniqueId)
        player.moveToLobby()
        loadScoreboard(player)
    }

    override fun unload(uuid: UUID) {
        Players.remove(uuid)?.let { save(it) }
    }

    override fun unload(player: Player) {
        Players.remove(player.uniqueId)?.let { save(it) }
    }

    override fun save(spleefPlayerInfo: SpleefPlayerInfo) = Database.executeUpdate(
            false,
            "UPDATE `${Table.name}` SET `rating` = ?, `wins` = ?, `draw` = ?, `lose` = ?, `coins` = ?, `items` = ? WHERE `name` = ?",
            spleefPlayerInfo.rating,
            spleefPlayerInfo.wins,
            spleefPlayerInfo.draw,
            spleefPlayerInfo.lose,
            spleefPlayerInfo.coins,
            Serializer.toJson(spleefPlayerInfo.items),
            spleefPlayerInfo.player.name
    )

    override fun saveAllPlayers() {
        Players.values.forEach { save(it) }
    }

    override fun getSpleefPlayerInfo(uuid: UUID): SpleefPlayerInfo? = Players[uuid]

    override fun getSpleefPlayerInfo(name: String): SpleefPlayerInfo? = getSpleefPlayerInfo(Bukkit.getPlayer(name).uniqueId)

    override fun getSpleefPlayerInfo(player: Player): SpleefPlayerInfo? = getSpleefPlayerInfo(player.uniqueId)

    private fun loadScoreboard(player: Player) = ApiManager.newScoreboardBuilder().apply {
        val spleefPlayer = getSpleefPlayerInfo(player)!!
        title = "§bSpleef"
        setLine(
                11,
                "§7${
                    LocalDateTime.ofInstant(
                            Instant.ofEpochMilli(System.currentTimeMillis()),
                            ZoneId.of("Europe/Moscow")
                    ).format(DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss"))
                }"
        )
        setLine(10, "")
        setLine(9, "§bСтатистика")
        setLine(8, "  §fРейтинг: ${spleefPlayer.rating}")
        setLine(7, "  §fПобед: ${spleefPlayer.wins}")
        setLine(6, "  §fНичьи: ${spleefPlayer.draw}")
        setLine(5, "  §fПоражений: ${spleefPlayer.lose}")
        setLine(4, "  §fПроцент побед: ${spleefPlayer.percentWin}%")
        setLine(3, "  §fМонет: ${spleefPlayer.coins}")
        setLine(2, "")
        setLine(1, "    §bwww.starfarm.fun")
        addUpdater(20) { _, board ->
            board.setLine(
                    11,
                    "§7${
                        LocalDateTime.ofInstant(
                                Instant.ofEpochMilli(System.currentTimeMillis()),
                                ZoneId.of("Europe/Moscow")
                        ).format(DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss"))
                    }"
            )
            board.setLine(8, "  §fРейтинг: ${spleefPlayer.rating}")
            board.setLine(7, "  §fПобед: ${spleefPlayer.wins}")
            board.setLine(6, "  §fНичьи: ${spleefPlayer.draw}")
            board.setLine(5, "  §fПоражений: ${spleefPlayer.lose}")
            board.setLine(4, "  §fПроцент побед: ${spleefPlayer.percentWin}%")
            board.setLine(3, "  §fМонет: ${spleefPlayer.coins}")
        }
    }.build(player)
}