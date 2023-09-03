package ru.starfarm.spleef.game.bars

import org.bukkit.Bukkit
import org.bukkit.boss.BarColor
import org.bukkit.boss.BarStyle
import ru.starfarm.core.util.format.Formatter
import ru.starfarm.spleef.game.GameInfo
import ru.starfarm.spleef.game.GameStateType
import ru.starfarm.spleef.player.SpleefPlayerInfo
import java.time.Duration
import java.time.Instant

/**
 * @author nolleNNN
 * @Date 02.09.2023
 * @Time 22:33
 */
class GameBar {
    private val bar = Bukkit.createBossBar("", BarColor.WHITE, BarStyle.SOLID)

    fun addBar(players: List<SpleefPlayerInfo>) = players.forEach { bar.addPlayer(it.player) }

    fun removeBar(players: List<SpleefPlayerInfo>) = players.forEach { bar.removePlayer(it.player) }

    fun updateBar(gameInfo: GameInfo) {
        val time =
            Formatter.formatTimeText(Duration.between(Instant.now(), Instant.now().plus(gameInfo.duration)).toMillis())

        if (gameInfo.state == GameStateType.ENDING)
            setTitle("§aИгра окончена", true)
        else
            setTitle("§fДо конца игры: §6$time", false)

    }

    private fun setTitle(title: String, positive: Boolean) {
        bar.title = title
        bar.color = if (positive) BarColor.GREEN else BarColor.RED
    }

}