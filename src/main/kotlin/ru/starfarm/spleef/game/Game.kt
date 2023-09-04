package ru.starfarm.spleef.game

import org.bukkit.GameMode
import org.bukkit.entity.Player
import org.bukkit.event.EventPriority
import org.bukkit.event.player.PlayerMoveEvent
import ru.starfarm.core.event.on
import ru.starfarm.spleef.Event
import ru.starfarm.spleef.MapService
import ru.starfarm.spleef.Task
import ru.starfarm.spleef.game.lobby.util.moveToLobby
import ru.starfarm.spleef.player.util.sendPlayerMessage
import ru.starfarm.spleef.player.util.sendPlayerTitle
import java.time.Instant
import java.util.concurrent.atomic.AtomicInteger

/**
 * @author nolleNNN
 * @Date 02.09.2023
 * @Time 22:30
 */
class Game {
    private lateinit var gameInfo: GameInfo
    private val counter = AtomicInteger()
    private val task = Task.fork(true)
    private val event = Event.fork(true)
    private var timer = 3
    fun startGame(firstPlayer: Player, secondPlayer: Player, mapId: String) {
        val loadedWorld = MapService.loadWorld("SPLEEF", mapId, "$mapId-${counter.getAndIncrement()}", true)
        gameInfo = GameInfo(firstPlayer, secondPlayer, mapId, loadedWorld.get())
        gameInfo.teleportToArenaSpawn()
        event.on<PlayerMoveEvent>(priority = EventPriority.HIGHEST) {
            if (gameInfo.gameState == GameStateType.WAITING) isCancelled = true
        }
        task.every(20, 20) { task ->
            if (timer == 0) {
                gameInfo.changeState(GameStateType.RUNNING)
                gameInfo.addBar()
                runningGame()
                task.cancel()
                return@every
            }
            gameInfo.players.forEach {
                it.player.sendPlayerTitle(
                    "§сДо начала игры", "$timer"
                )
            }
            timer--
        }

    }

    private fun runningGame() {
        task.everyAsync(1, 1) {
            if (Instant.now().isAfter(gameInfo.endStamp)) {
                drawGame()
                gameInfo.changeState(GameStateType.ENDING)
                gameInfo.updateBar()
                it.cancel()
                return@everyAsync
            }
            gameInfo.updateBar()
        }
        event.on<PlayerMoveEvent> {
            if (gameInfo.zone.contains(player) && gameInfo.gameState != GameStateType.ENDING) {
                gameInfo.changeState(GameStateType.ENDING)
                player.gameMode = GameMode.SPECTATOR
                endGame()
            }
        }
    }


    private fun drawGame() {
        gameInfo.players.forEach {
            it.coins += 15
            it.draw++
        }
        task.asyncAfter(20 * 3) { close() }
    }

    private fun endGame() {
        gameInfo.players.forEach {
            if (it.player.gameMode != GameMode.SPECTATOR) {
                it.wins++
                it.rating += 5
                it.coins += 50
                it.player.sendPlayerMessage("§aВы победили и получили §650 §aмонет!")
            } else {
                it.lose++
                it.rating -= 5
                it.coins += 10
                it.player.sendPlayerMessage("§cВы проиграли.. Вы получили §610 §смонет!")
            }
        }
        task.asyncAfter(20 * 3) { close() }
    }

    private fun close() {
        gameInfo.players.forEach { it.player.moveToLobby() }
        gameInfo.unloadWorld()
        gameInfo.removeBar()
        task.close()
        event.close()
    }
}