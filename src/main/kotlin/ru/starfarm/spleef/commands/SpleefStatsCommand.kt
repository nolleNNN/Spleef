package ru.starfarm.spleef.commands

import org.bukkit.entity.Player
import ru.starfarm.core.command.Command
import ru.starfarm.core.command.base.annotation.BaseCommand
import ru.starfarm.core.command.base.annotation.BaseCommandParameter
import ru.starfarm.core.command.base.annotation.BaseCommandPrefix
import ru.starfarm.core.command.context.CommandContext
import ru.starfarm.spleef.player.util.Prefix
import ru.starfarm.spleef.player.util.coloredName
import ru.starfarm.spleef.player.util.spleefPlayer

/**
 * @author nolleNNN
 * @Date 03.09.2023
 * @Time 15:18
 */
@BaseCommand("stats", "Показать статистику игрока")
@BaseCommandPrefix(Prefix)
object SpleefStatsCommand : Command<Player>() {
    @BaseCommandParameter("Игрок", "Player", required = false)
    override fun execute(ctx: CommandContext<Player>) {
        val player = if (ctx.hasArg(0)) ctx.getArg<Player>(0)?.spleefPlayer else ctx.sender.spleefPlayer
        ctx.sendMessage("Статистика игрока ${player?.player?.coloredName}")
        ctx.sendMessage("Количестов игр - ${player?.gameAmount}")
        ctx.sendMessage("Количестов побед - ${player?.wins}")
        ctx.sendMessage("Количестов поражений - ${player?.lose}")
        ctx.sendMessage("Количество игр сыгранных в ничью - ${player?.draw}")
        ctx.sendMessage("Процент побед - ${player?.percentWin}%%")
    }
}