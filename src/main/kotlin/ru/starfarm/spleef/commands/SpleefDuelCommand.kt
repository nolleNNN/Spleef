package ru.starfarm.spleef.commands

import net.md_5.bungee.api.ChatMessageType
import org.bukkit.entity.Player
import ru.starfarm.core.command.Command
import ru.starfarm.core.command.context.CommandContext
import ru.starfarm.core.command.patameter.tab.PlayersCompleter
import ru.starfarm.core.command.type.TypePlayer
import ru.starfarm.core.util.format.ChatUtil
import ru.starfarm.core.util.format.message.hover.HoverEventType
import ru.starfarm.spleef.Task
import ru.starfarm.spleef.game.Game
import ru.starfarm.spleef.game.lobby.LobbyService
import ru.starfarm.spleef.player.SpleefPlayerInfo
import ru.starfarm.spleef.player.util.PREFIX
import ru.starfarm.spleef.player.util.coloredName
import ru.starfarm.spleef.player.util.sendPlayerMessage
import ru.starfarm.spleef.player.util.spleefPlayer

/**
 * @author nolleNNN
 * @Date 03.09.2023
 * @Time 15:30
 */
object SpleefDuelCommand : Command<Player>("duel", "Отправить дуэль игроку") {
    private val players = mutableMapOf<SpleefPlayerInfo, SpleefPlayerInfo>()
    init {
        prefix = PREFIX

        addCommand(SpleefDenyDuelCommand)

        commands.forEach {
            it.prefix = prefix
        }

        addParameter("Игрок", TypePlayer(), true, PlayersCompleter())
    }
    override fun execute(ctx: CommandContext<Player>) {
        val sender = ctx.sender.spleefPlayer!!
        val target = ctx.getArg<Player>(0)!!.spleefPlayer!!

        if (target.player == sender.player) {
            ctx.sendMessage("§cНельзя отправить приглашение самому себе!")
            return
        }

        players[sender] = target

        ctx.sendMessage("§aВы успешно отправили запрос на дуэль игроку ${target.player.coloredName}")

        target.player.spigot().sendMessage(ChatMessageType.CHAT,
            ChatUtil.newBuilder()
                .text("§aНажмите, чтобы отказаться от дуэли!")
                .hover(
                    HoverEventType.SHOW_TEXT,
                    "§aНик отправителя: ${sender.player.coloredName}",
                    "§fКоличество рейтинга: §6${sender.rating}"
                )
                .clickRunCommand("/duel deny ${sender.player.name}")
                .build()
        )
        target.player.sendPlayerMessage("Если через 10 секунд вы не отмените дуэль, то игра автоматически запустится с вами!")
        Task.everyAsync(20, 20, 10) {
            if (players[sender] == null) {
                it.cancel()
                return@everyAsync
            }
            if (it.periods >= 10) {
                Game().startGame(sender.player, target.player, LobbyService.maps[0])
                it.cancel()
                return@everyAsync
            }

        }
    }

    object SpleefDenyDuelCommand : Command<Player>("deny", "Принять дуэль от игрока", "d") {
        init {
            addParameter("Игрок", TypePlayer(), true, PlayersCompleter())
        }

        override fun execute(ctx: CommandContext<Player>) {
            val target = ctx.getArg<Player>(0)!!.spleefPlayer!!

            if (target.player == ctx.sender) {
                ctx.sendMessage("§cНельзя дать отказ самому себе!")
                return
            }

            players.remove(target)
            ctx.sendMessage("§cВы отказались от дуэли!")
            target.player.sendPlayerMessage("§aПротивник отказался от дуэли")
        }
    }
}

