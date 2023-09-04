package ru.starfarm.spleef.game

import org.bukkit.Bukkit
import org.bukkit.GameMode
import org.bukkit.entity.Player
import ru.starfarm.map.world.LoadedWorld
import ru.starfarm.spleef.game.bar.GameBar
import ru.starfarm.spleef.game.lobby.util.removeLobbyItem
import ru.starfarm.spleef.player.util.spleefPlayer
import java.time.Duration
import java.time.Instant

/**
 * @author nolleNNN
 * @Date 02.09.2023
 * @Time 22:28
 */
data class GameInfo(
    private val firstPlayer: Player,
    private val secondPlayer: Player,
    private val mapId: String,
    private val map: LoadedWorld,
    private val gameBar: GameBar = GameBar(),
    private var state: GameStateType = GameStateType.WAITING,
    private val duration: Duration = Duration.ofMinutes(3),
) {

    val zone get() = map.getCuboid(mapId)!!
    val players get() = mutableListOf(firstPlayer.spleefPlayer!!, secondPlayer.spleefPlayer!!)
    val gameState get() = state
    val endStamp: Instant get() = Instant.now().plus(duration)

    fun teleportToArenaSpawn() {
        firstPlayer.teleport(map.getPoint(mapId, "firstLocation"))
        secondPlayer.teleport(map.getPoint(mapId, "secondLocation"))
        players.forEach {
            it.player.removeLobbyItem()
            it.addGameItem()
            it.player.gameMode = GameMode.SURVIVAL
        }
    }

    fun updateBar() = gameBar.updateBar(this)

    fun addBar() = gameBar.addBar(players)
    fun removeBar() = gameBar.removeBar(players)

    fun changeState(gameStateType: GameStateType) {
        state = gameStateType
    }

    fun unloadWorld() = map.unloadWorld()
}

fun LoadedWorld.unloadWorld() = Bukkit.unloadWorld(world, false)