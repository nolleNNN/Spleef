package ru.starfarm.spleef.test.stages

import org.bukkit.GameMode
import org.bukkit.event.player.PlayerMoveEvent
import ru.starfarm.core.event.GlobalEventContext.on
import ru.starfarm.spleef.lobby.util.removeLobbyItem
import ru.starfarm.spleef.player.SpleefPlayerInfo
import ru.starfarm.spleef.player.util.sendPlayerTitle
import ru.starfarm.spleef.player.util.spleefPlayer
import ru.starfarm.spleef.test.Stage

/**
 * @author nolleNNN
 * @Date 07.09.2023
 * @Time 19:00
 */
class GameStage(private val firstPlayer: SpleefPlayerInfo, private val secondPlayer: SpleefPlayerInfo) : Stage() {
    init {
        startStage()
    }

    private val players = hashSetOf(firstPlayer.player, secondPlayer.player)
    private var time = 3

    override fun startStage() {
        players.forEach { it.spleefPlayer?.addGameItem() }
        tickStage()
    }

    override fun tickStage() {
        taskContext.everyAsync(20, 20) {
            if (time <= 0) {
                players.forEach {
                    it.sendPlayerTitle("§aВперед!", "§aПобедит сильнейший!")
                    it.removeLobbyItem()
                    it.spleefPlayer?.addGameItem()
                }
                return@everyAsync
            }
            players.forEach {
                it.sendPlayerTitle("§fДо начала игры", "$time")
            }
            time--
        }
        eventContext.on<PlayerMoveEvent> {
            if (time <= 0) isCancelled = true
            if (player.location.y <= 70) {
                player.gameMode = GameMode.SPECTATOR
                endStage()
            }
        }
    }

    override fun endStage(): Stage = GameEndStage(firstPlayer, secondPlayer)
}