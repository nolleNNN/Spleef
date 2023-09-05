package ru.starfarm.spleef.commands

import org.bukkit.entity.Player
import ru.starfarm.core.command.Command
import ru.starfarm.core.command.context.CommandContext
import ru.starfarm.core.command.patameter.tab.PlayersCompleter
import ru.starfarm.core.command.require.Require
import ru.starfarm.core.command.type.TypeInteger
import ru.starfarm.core.command.type.TypePlayer
import ru.starfarm.spleef.player.util.PREFIX
import ru.starfarm.spleef.player.util.coloredName
import ru.starfarm.spleef.player.util.spleefPlayer

/**
 * @author nolleNNN
 * @Date 02.09.2023
 * @Time 20:33
 */
object SpleefCommand : Command<Player>("spleef", "Для работы с режимом", "s", "sp") {

    init {
        prefix = PREFIX

        addCommand(GiveMoneyCommand)

        commands.forEach {
            it.prefix = prefix
            it.addRequire(Require.Companion.permission("*"))
        }

    }

    override fun execute(ctx: CommandContext<Player>) {
        generateHelp(ctx.sender)
    }

    object GiveMoneyCommand : Command<Player>("give", "Выдать монеты") {
        init {

            addParameter("Количество монет", TypeInteger())
            addParameter("Игрок", TypePlayer(), false, PlayersCompleter())
        }

        override fun execute(ctx: CommandContext<Player>) {
            val amount = ctx.getArg<Int>(0)
            val player = if (ctx.hasArg(1)) ctx.getArg<Player>(1)!! else ctx.sender
            player.spleefPlayer!!.coins += amount!!
            ctx.sendMessage(
                "§aВы успешно выдали §6$amount §aмонет " +
                        if (player.name != ctx.sender.name) "игроку ${player.coloredName}" else "себе"
            )
        }
    }

}