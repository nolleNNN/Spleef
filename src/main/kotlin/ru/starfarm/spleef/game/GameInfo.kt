package ru.starfarm.spleef.game

import org.bukkit.entity.Player
import ru.starfarm.map.world.LoadedWorld
import ru.starfarm.spleef.MapService
import ru.starfarm.spleef.game.bars.GameBar
import ru.starfarm.spleef.player.util.spleefPlayer
import java.time.Duration
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
    val mapId: String = listOf("first", "second")[ThreadLocalRandom.current().nextInt(2)],
    val gameBar: GameBar = GameBar(),
    val duration: Duration = Duration.ofMinutes(3),
    var state: GameStateType = GameStateType.WAITING,
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

    fun teleportToArenaSpawn() {
        val firstLocation = map.getPoint(mapId, "firstLocation")
        val secondLocation = map.getPoint(mapId, "secondLocation")
        firstPlayer.teleport(firstLocation)
        secondPlayer.teleport(secondLocation)
        players.forEach { it.addItem() }
        gameBar.addBar(players)
    }

    fun updateBar() = gameBar.updateBar(this)

    fun changeState(gameStateType: GameStateType) {
        state = gameStateType
    }
}
