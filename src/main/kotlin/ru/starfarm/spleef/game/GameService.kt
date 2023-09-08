package ru.starfarm.spleef.game

import org.bukkit.entity.Player
import ru.starfarm.spleef.MapService
import ru.starfarm.spleef.game.bar.GameBar
import ru.starfarm.spleef.player.util.spleefPlayer
import java.util.*
import java.util.concurrent.ThreadLocalRandom
import java.util.concurrent.atomic.AtomicInteger

/**
 * @author nolleNNN
 * @Date 05.09.2023
 * @Time 19:57
 */
object GameService {
    private val maps: MutableList<String> = mutableListOf("first", "second")
    private val counter = AtomicInteger()
    private val games = mutableMapOf<UUID, GameStage>()

    fun createGame(firstPlayer: Player, secondPlayer: Player) {
        val mapId = maps[ThreadLocalRandom.current().nextInt(maps.size - 1)]
        val loadedWorld = MapService.loadWorld("SPLEEF", mapId, "$mapId-${counter.getAndIncrement()}").get()
        val game = Game(firstPlayer.spleefPlayer!!, secondPlayer.spleefPlayer!!, GameBar(), mapId, loadedWorld)
        games[UUID.randomUUID()] = game
    }

    fun getGame(uuid: UUID) = games[uuid]
}