package ru.starfarm.spleef.test.stages

import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import ru.starfarm.core.event.GlobalEventContext.on
import ru.starfarm.core.util.format.ChatUtil
import ru.starfarm.core.util.number.NumberUtil
import ru.starfarm.spleef.lobby.util.moveToWaitingLobby
import ru.starfarm.spleef.player.SpleefPlayerInfo
import ru.starfarm.spleef.player.util.coloredName
import ru.starfarm.spleef.player.util.sendPlayerMessage
import ru.starfarm.spleef.test.Stage

/**
 * @author nolleNNN
 * @Date 07.09.2023
 * @Time 18:59
 */
class LobbyStage(private val first: SpleefPlayerInfo, private val second: SpleefPlayerInfo) : Stage() {
    init {
        startStage()
    }

    private val players = hashSetOf(first.player, second.player)
    private var time = 10

    override fun startStage() {
        players.forEach { it.moveToWaitingLobby() }
        tickStage()
    }

    override fun tickStage() {
        taskContext.everyAsync(20, 20) {
            if (players.size <= 1) {
                time = 10
                return@everyAsync
            }
            if (time == 0) {
                endStage()
                return@everyAsync
            }
            if (time < 5)
                players.forEach { it.sendPlayerMessage("§aДо начала игры осталось §6${NumberUtil.getTime(time)}") }
            time--
        }
        eventContext.on<PlayerJoinEvent> {
            joinMessage = null
            ChatUtil.broadcast("§aИгрок ${player.coloredName} §aприсоединился к игре!")
            players.add(player)
        }
        eventContext.on<PlayerQuitEvent> {
            quitMessage = null
            ChatUtil.broadcast("§cИгрок ${player.coloredName} §cпокинул игру!")
            players.remove(player)
        }
    }

    override fun endStage(): Stage = GameStage(first, second)
}