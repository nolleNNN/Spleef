package ru.starfarm.spleef.game

import org.bukkit.GameMode
import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerMoveEvent
import ru.starfarm.core.event.on
import ru.starfarm.spleef.Event
import ru.starfarm.spleef.Task
import ru.starfarm.spleef.game.lobby.util.moveToLobby
import ru.starfarm.spleef.player.util.sendPlayerMessage
import java.time.Instant

/**
 * @author nolleNNN
 * @Date 02.09.2023
 * @Time 22:30
 */
class Game {
    private lateinit var gameInfo: GameInfo
    fun startGame(firstPlayer: Player, secondPlayer: Player) {
        gameInfo = GameInfo(firstPlayer, secondPlayer)
        gameInfo.teleportToArenaSpawn()
        Event.on<PlayerMoveEvent> { if (gameInfo.state == GameStateType.WAITING) isCancelled = true }
        Task.asyncAfter(20 * 3) {
            gameInfo.changeState(GameStateType.RUNNING)
            runningGame()
        }
    }

    private fun runningGame() {
        val time = Instant.now().plus(gameInfo.duration)
        Task.everyAsync(20, 20) {
            if (Instant.now().isAfter(time)) {
                drawGame()
                gameInfo.changeState(GameStateType.ENDING)
                gameInfo.updateBar()
                it.cancel()
                return@everyAsync
            }
            gameInfo.updateBar()
        }
        Event.on<PlayerMoveEvent> {
            if (gameInfo.zone.contains(player) && gameInfo.state != GameStateType.ENDING) {
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
            it.player.moveToLobby()
        }
        gameInfo.gameBar.removeBar(gameInfo.players)
    }

    private fun endGame() {
        gameInfo.players.forEach {
            if (it.player.gameMode == GameMode.SPECTATOR) {
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
            it.player.moveToLobby()
        }
        gameInfo.gameBar.removeBar(gameInfo.players)
    }
}