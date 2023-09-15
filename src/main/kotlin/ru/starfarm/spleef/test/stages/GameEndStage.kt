package ru.starfarm.spleef.test.stages

import org.bukkit.GameMode
import ru.starfarm.spleef.lobby.util.moveToLobby
import ru.starfarm.spleef.player.SpleefPlayerInfo
import ru.starfarm.spleef.player.util.sendPlayerMessage
import ru.starfarm.spleef.player.util.spleefPlayer
import ru.starfarm.spleef.test.Stage

/**
 * @author nolleNNN
 * @Date 07.09.2023
 * @Time 19:01
 */
class GameEndStage(first: SpleefPlayerInfo, second: SpleefPlayerInfo) : Stage() {

    init {
        startStage()
    }

    private val players = hashSetOf(first.player, second.player)

    override fun startStage() {
        tickStage()
        players.forEach {
            when (it.gameMode) {
                GameMode.SPECTATOR -> {
                    it.spleefPlayer?.lose = it.spleefPlayer?.lose!! + 1
                    it.spleefPlayer?.coins = it.spleefPlayer?.coins!! + 10
                    it.spleefPlayer?.rating = it.spleefPlayer?.rating!! - 5
                    it.sendPlayerMessage("§fВы проиграли! Но ничего, в следующий раз повезет!")
                }

                GameMode.SURVIVAL -> {
                    it.spleefPlayer?.wins = it.spleefPlayer?.wins!! + 1
                    it.spleefPlayer?.coins = it.spleefPlayer?.coins!! + 50
                    it.spleefPlayer?.rating = it.spleefPlayer?.rating!! + 5
                    it.sendPlayerMessage("§fВы победили! Так держать!")
                }

                else -> {}
            }
        }

    }

    override fun tickStage() {
        taskContext.everyAsync(20, 20, 5) { task ->
            if (task.periods == 0) {
                players.forEach {
                    it.moveToLobby()
                }
                return@everyAsync
            }
            players.forEach {
                it.sendPlayerMessage("Вы будете перемещены в лобби через ${task.periods}")
            }
        }
    }

    override fun endStage(): Stage? = null
}