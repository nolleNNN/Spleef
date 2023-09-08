package ru.starfarm.spleef.player

import com.google.gson.reflect.TypeToken
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.scheduler.BukkitRunnable
import ru.starfarm.core.ApiManager
import ru.starfarm.core.database.query.row.ValueQueryRow
import ru.starfarm.core.util.serializer.Serializer
import ru.starfarm.spleef.Database
import ru.starfarm.spleef.DatabaseConnection
import ru.starfarm.spleef.Plugin
import ru.starfarm.spleef.items.ItemService
import ru.starfarm.spleef.lobby.util.moveToLobby
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*
import java.util.concurrent.ConcurrentMap
import java.util.concurrent.TimeUnit


/**
 * @author nolleNNN
 * @Date 02.09.2023
 * @Time 20:57
 */
object SpleefPlayerService : BukkitRunnable() {
    private val players = mutableMapOf<String, SpleefPlayerInfo>()
    private val Items = object : TypeToken<ConcurrentMap<Int, Boolean>>() {}.type
    private val Table = DatabaseConnection.getTable("spleef_players")

    init {
        runTaskTimerAsynchronously(Plugin, 1L, 20L * TimeUnit.MINUTES.toSeconds(5))
    }

    fun load(uuid: UUID) = load(Bukkit.getPlayer(uuid))

    fun load(player: Player) {
        Table.newDatabaseQuery()
            .selectQuery()
            .queryRow(ValueQueryRow("name", player.name))
            .executeQueryAsync(DatabaseConnection)
            .thenAccept {
                val spleefPlayerInfo: SpleefPlayerInfo
                if (!it.next()) {
                    Table.newDatabaseQuery()
                        .insertQuery()
                        .queryRow(ValueQueryRow("name", player.name))
                        .queryRow(ValueQueryRow("rating", 0))
                        .queryRow(ValueQueryRow("wins", 0))
                        .queryRow(ValueQueryRow("draw", 0))
                        .queryRow(ValueQueryRow("lose", 0))
                        .queryRow(ValueQueryRow("coins", 0))
                        .queryRow(ValueQueryRow("items", ItemService.playerItems))
                        .executeAsync(DatabaseConnection)

                    spleefPlayerInfo = SpleefPlayerInfo(player, ItemService.playerItems)

                    players[player.name] = spleefPlayerInfo
                    return@thenAccept
                }
                spleefPlayerInfo = SpleefPlayerInfo(
                    player,
                    Serializer.fromJson(it.getString("items"), Items),
                    it.getInt("rating"),
                    it.getInt("wins"),
                    it.getInt("draw"),
                    it.getInt("lose"),
                    it.getInt("coins")
                )
                players[player.name] = spleefPlayerInfo
                it.close()
            }
        player.moveToLobby()
        loadScoreboard(player)
    }

    fun unload(player: Player) = save(players.remove(player.name)!!)

    private fun save(spleefPlayerInfo: SpleefPlayerInfo) = Database.executeUpdate(
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

    private fun loadScoreboard(player: Player) = ApiManager.newScoreboardBuilder().apply {
        val spleefPlayer = getSpleefPlayer(player)!!
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

    fun getSpleefPlayer(player: Player): SpleefPlayerInfo? = players[player.name]
    override fun run() {
        players.values.forEach { save(it) }
    }

}