package ru.starfarm.spleef.game

import org.bukkit.Bukkit
import org.bukkit.GameMode
import ru.starfarm.spleef.Event
import ru.starfarm.spleef.Task
import ru.starfarm.spleef.game.type.GameStateType
import ru.starfarm.spleef.lobby.util.moveToLobby
import ru.starfarm.spleef.player.SpleefPlayerInfo
import ru.starfarm.spleef.player.util.spleefPlayer

/**
 * @author nolleNNN
 * @Date 04.09.2023
 * @Time 18:56
 */
abstract class GameStage {
    val taskContext = Task.fork(true)
    val eventContext = Event.fork(true)
    var stateType: GameStateType = GameStateType.WAITING
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
            } else {
                it.wins++
                it.rating += 5
                it.coins += 15
            }
            it.player.moveToLobby()
        }
        close()
    }

    fun leaveGame(leaver: SpleefPlayerInfo) {
        leaver.lose++
        if (leaver.rating - 5 < 0) leaver.rating = 0
        else leaver.rating -= 5
        leaver.coins += 5

        val winner = Bukkit.getWorld(leaver.player.world.name).players.first { it != leaver.player }.spleefPlayer!!
        winner.wins++
        winner.rating += 5
        winner.coins += 15
        winner.player.moveToLobby()
        close()
    }

    fun drawGame(players: MutableList<SpleefPlayerInfo>) {
        players.forEach {
            it.draw++
            it.coins += 10
            it.player.moveToLobby()
        }
        close()
    }

    fun changeGameType(gameStateType: GameStateType) {
        stateType = gameStateType
        if (stateType == GameStateType.ENDING) endStage()
    }

    private fun close() {
        taskContext.close()
        eventContext.close()
    }
}