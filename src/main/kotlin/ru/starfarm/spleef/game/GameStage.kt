package ru.starfarm.spleef.game

import org.bukkit.GameMode
import ru.starfarm.spleef.Event
import ru.starfarm.spleef.Task
import ru.starfarm.spleef.game.type.GameStageType
import ru.starfarm.spleef.lobby.util.moveToLobby
import ru.starfarm.spleef.player.SpleefPlayerInfo
import ru.starfarm.spleef.player.util.sendPlayerMessage

/**
 * @author nolleNNN
 * @Date 04.09.2023
 * @Time 18:56
 */
abstract class GameStage {
    val taskContext = Task.fork(true)
    val eventContext = Event.fork(true)
    var stateType = GameStageType.WAITING
    abstract fun waitingStage()
    abstract fun startStage()
    abstract fun runStage()
    abstract fun endStage()

    fun winGame(players: MutableList<SpleefPlayerInfo>) {
        players.forEach {
            if (it.player.gameMode == GameMode.SPECTATOR) {
                it.lose++
                if (it.rating - 5 < 0) it.rating = 0
                else it.rating -= 5
                it.coins += 5
                it.player.sendPlayerMessage("§aВы проиграли и потеряли §65 §aрейтинга и получили §65 §aмонет")
                it.player.moveToLobby()
            } else {
                it.wins++
                it.rating += 5
                it.coins += 15
                it.player.moveToLobby()
                it.player.sendPlayerMessage("§aВы победили и получили §65 §aрейтинга и §615 §aмонет")
            }
            players.remove(it)
        }
        close()
    }

    fun leaveGame(players: MutableList<SpleefPlayerInfo>) {
        players.forEach {
            if (!it.player.isOnline) {
                it.lose++
                if (it.rating - 5 < 0) it.rating = 0
                else it.rating -= 5
                it.coins += 5
            } else {
                it.wins++
                it.rating += 5
                it.coins += 15
                it.player.moveToLobby()
                it.player.sendPlayerMessage("§aВы победили, так как противник покинул игру, и получили §65 §aрейтинга и §615 §aмонет")
            }
            players.remove(it)
        }
        close()
    }

    fun drawGame(players: MutableList<SpleefPlayerInfo>) {
        players.forEach {
            it.draw++
            it.coins += 10
            it.player.moveToLobby()
            it.player.sendPlayerMessage("§aНикто не победил!")
            players.remove(it)
        }
        close()
    }

    fun changeGameType(gameStageType: GameStageType) {
        stateType = gameStageType
        if (stateType == GameStageType.ENDING) endStage()
    }

    private fun close() {
        taskContext.close()
        eventContext.close()
    }
}