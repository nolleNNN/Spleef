package ru.starfarm.spleef.game

import org.bukkit.GameMode
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.event.player.PlayerQuitEvent
import ru.starfarm.core.event.on
import ru.starfarm.map.world.LoadedWorld
import ru.starfarm.spleef.game.bar.GameBar
import ru.starfarm.spleef.game.type.GameStateType
import ru.starfarm.spleef.game.util.unloadWorld
import ru.starfarm.spleef.lobby.util.moveToLobby
import ru.starfarm.spleef.player.SpleefPlayerInfo
import ru.starfarm.spleef.player.util.sendPlayerTitle
import ru.starfarm.spleef.player.util.spleefPlayer
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
    private val players get() = mutableListOf(firstPlayer, secondPlayer)
    private val zone get() = map.getCuboid(mapId)
    private val firstLocation get() = map.getPoint("firstLocation")
    private val secondLocation get() = map.getPoint("secondLocation")
    override fun waitingStage() {
        var time = 3
        taskContext.everyAsync(20, 20) {
            players.forEach {
                it.player.sendPlayerTitle("До начала игры осталось", "$time")
            }
            time--
        }
        startStage()
    }

    override fun startStage() {
        firstPlayer.player.teleport(firstLocation)
        secondPlayer.player.teleport(secondLocation)
        gameBar.addBar(players)
        runStage()
    }

    override fun runStage() {
        val endStamp = Instant.now().plus(Duration.ofMinutes(3))
        taskContext.everyAsync(20, 20) {
            if (Instant.now().isAfter(endStamp)) {
                drawGame(players)
                changeGameType(GameStateType.ENDING)
                it.cancel()
                return@everyAsync
            }
            gameBar.updateBar(endStamp, stateType)
        }
        eventContext.on<PlayerQuitEvent> { leaveGame(player.spleefPlayer!!) }
        eventContext.on<PlayerMoveEvent> {
            if (zone!!.contains(player) && stateType != GameStateType.ENDING) {
                player.gameMode = GameMode.SPECTATOR
                changeGameType(GameStateType.ENDING)
                winGame(players)
            }
        }
    }

    override fun endStage() {
        gameBar.removeBar(players)
        players.forEach {
            it.player.moveToLobby()
        }
        map.unloadWorld()
    }
}