package ru.starfarm.spleef.game

import org.bukkit.GameMode
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.event.player.PlayerQuitEvent
import ru.starfarm.core.event.on
import ru.starfarm.map.world.LoadedWorld
import ru.starfarm.spleef.game.bar.GameBar
import ru.starfarm.spleef.game.type.GameStageType
import ru.starfarm.spleef.game.util.unloadWorld
import ru.starfarm.spleef.lobby.util.moveToLobby
import ru.starfarm.spleef.player.SpleefPlayerInfo
import ru.starfarm.spleef.player.util.sendPlayerTitle
import java.time.Duration
import java.time.Instant

/**
 * @author nolleNNN
 * @Date 04.09.2023
 * @Time 18:58
 */
class Game(
    private val firstPlayer: SpleefPlayerInfo,
    private val secondPlayer: SpleefPlayerInfo,
    private val gameBar: GameBar,
    private val mapId: String,
    private val map: LoadedWorld,
) : GameStage() {
    init {
        waitingStage()
    }
    private val players get() = mutableListOf(firstPlayer, secondPlayer)
    private val zone get() = map.getCuboid(mapId)
    private val firstLocation get() = map.getPoint(mapId,"firstLocation")
    private val secondLocation get() = map.getPoint(mapId, "secondLocation")
    override fun waitingStage() {
        firstPlayer.player.teleport(firstLocation)
        secondPlayer.player.teleport(secondLocation)
        var time = 3
        taskContext.everyAsync(20, 20) {task ->
            if (time == 0) {
                players.forEach {
                    it.addGameItem()
                    it.player.gameMode = GameMode.SURVIVAL
                    players.remove(it)
                }
                startStage()
                task.cancel()
                return@everyAsync
            }
            players.forEach {
                it.player.sendPlayerTitle("До начала игры осталось", "$time")
            }
            time--
        }
        taskContext.everyAsync(1, 1) {
            if (time == 0) {
                it.cancel()
                return@everyAsync
            }
            firstPlayer.player.teleport(firstLocation)
            secondPlayer.player.teleport(secondLocation)
        }
    }

    override fun startStage() {
        gameBar.addBar(players)
        changeGameType(GameStageType.RUNNING)
        runStage()
    }

    override fun runStage() {
        val endStamp = Instant.now().plus(Duration.ofMinutes(3))
        taskContext.everyAsync(20, 20) {
            if (Instant.now().isAfter(endStamp)) {
                drawGame(players)
                changeGameType(GameStageType.ENDING)
                it.cancel()
                return@everyAsync
            }
            gameBar.updateBar(endStamp, stateType)
        }
        eventContext.on<PlayerQuitEvent> { leaveGame(players) }
        eventContext.on<PlayerMoveEvent> {
            if (zone!!.contains(player) && stateType != GameStageType.ENDING) {
                player.gameMode = GameMode.SPECTATOR
                changeGameType(GameStageType.ENDING)
                winGame(players)
            }
        }
    }

    override fun endStage() {
        gameBar.removeBar(players)
        players.forEach {
            it.player.moveToLobby()
            players.remove(it)
        }
        map.unloadWorld()
    }
}