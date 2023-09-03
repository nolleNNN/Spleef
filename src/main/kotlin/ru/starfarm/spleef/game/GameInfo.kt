package ru.starfarm.spleef.game

import org.bukkit.Bukkit
import org.bukkit.entity.Player
import ru.starfarm.map.world.LoadedWorld
import ru.starfarm.spleef.MapService
import ru.starfarm.spleef.game.bars.GameBar
import ru.starfarm.spleef.game.lobby.util.removeItem
import ru.starfarm.spleef.player.util.spleefPlayer
import java.time.Duration
import java.time.Instant
import java.util.concurrent.ThreadLocalRandom
import java.util.concurrent.atomic.AtomicInteger

/**
 * @author nolleNNN
 * @Date 02.09.2023
 * @Time 22:28
 */
data class GameInfo(
    private val firstPlayer: Player,
    private val secondPlayer: Player,
    private val gameBar: GameBar = GameBar(),
    private var state: GameStateType = GameStateType.WAITING,
    private val duration: Duration = Duration.ofMinutes(3),
    private val mapId: String = listOf("first", "second")[ThreadLocalRandom.current().nextInt(2)],
    private val map: LoadedWorld = MapService.loadWorld(
        "SPLEEF",
        mapId,
        "$mapId-${AtomicInteger().getAndIncrement()}",
        true
    )
        .get(),
) {

    val zone get() = map.getCuboid(mapId)!!
    val players get() = mutableListOf(firstPlayer.spleefPlayer!!, secondPlayer.spleefPlayer!!)
    val time: Instant get() = Instant.now().plus(duration)
    val gameState get() = state

    fun teleportToArenaSpawn() {
        firstPlayer.teleport(map.getPoint(mapId, "firstLocation"))
        secondPlayer.teleport(map.getPoint(mapId, "secondLocation"))
        players.forEach {
            it.player.removeItem()
            it.addItem()
        }
        gameBar.addBar(players)
    }

    fun updateBar() = gameBar.updateBar(this)

    fun removeBar() = gameBar.removeBar(players)

    fun changeState(gameStateType: GameStateType) {
        state = gameStateType
    }

    fun unloadWorld() = map.unloadWorld()
}

fun LoadedWorld.unloadWorld() {
    Bukkit.unloadWorld(world, false)
}