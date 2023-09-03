package ru.starfarm.spleef.commands

import org.bukkit.entity.Player
import ru.starfarm.core.command.Command
import ru.starfarm.core.command.context.CommandContext
import ru.starfarm.core.command.patameter.tab.PlayersCompleter
import ru.starfarm.core.command.type.TypePlayer
import ru.starfarm.spleef.player.util.PREFIX
import ru.starfarm.spleef.player.util.profile
import ru.starfarm.spleef.player.util.spleefPlayer

/**
 * @author nolleNNN
 * @Date 03.09.2023
 * @Time 15:18
 */
class SpleefStatsCommand : Command<Player>("stats", "Показать статистику игрока") {
    init {
        prefix = PREFIX

        addParameter("Игрок", TypePlayer(), false, PlayersCompleter())
    }
    override fun execute(ctx: CommandContext<Player>) {
        val player = if (ctx.hasArg(0)) ctx.getArg<Player>(0)!!.spleefPlayer!! else ctx.sender.spleefPlayer!!
        ctx.sendMessage("Статистика игрока ${player.player.profile!!.coloredName}")
        ctx.sendMessage("Количестов игр - ${player.wins + player.lose}")
        ctx.sendMessage("Количестов побед - ${player.wins}")
        ctx.sendMessage("Количестов поражений - ${player.lose}")
        ctx.sendMessage("Количество игр сыгранных в ничью - ${player.draw}")
        ctx.sendMessage("Процент побед - ${player.wins/player.lose}%")
    }


}