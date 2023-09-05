package ru.starfarm.spleef.lobby

import org.bukkit.entity.Player
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent
import ru.starfarm.core.entity.PlayerInteractFakeEntityEvent
import ru.starfarm.core.entity.impl.FakePlayer
import ru.starfarm.core.entity.type.Interact
import ru.starfarm.core.event.on
import ru.starfarm.spleef.Event
import ru.starfarm.spleef.Task
import ru.starfarm.spleef.game.GameService
import ru.starfarm.spleef.lobby.util.hasItemInMainHand
import ru.starfarm.spleef.menu.ItemBuyMenu
import ru.starfarm.spleef.npcs.NpcService
import ru.starfarm.spleef.npcs.updateName
import ru.starfarm.spleef.player.util.*

/**
 * @author nolleNNN
 * @Date 03.09.2023
 * @Time 14:21
 */
object LobbyService {
    private val clicks = mutableSetOf<Player>()
    val players = mutableListOf<Player>()
    val maps = listOf("first", "second")

    init {
        Event.on<PlayerInteractFakeEntityEvent> {
            if (interact == Interact.ATTACK) {
                if (entity == NpcService.get(1)!!.fakePlayer && !clicks.contains(player)) {
                    player.add(players, clicks)
                    player.sendPlayerMessage("§aВы успешно добавлены в очередь!")
                } else {
                    player.remove(players, clicks)
                    player.sendPlayerMessage("§cВы покинули очередь!")
                }
                (entity as FakePlayer).updateName()
            }
        }
        Event.on<PlayerInteractEvent> {
            if (player.hasItemInMainHand() && (action == Action.RIGHT_CLICK_BLOCK || action == Action.RIGHT_CLICK_AIR))
                ItemBuyMenu().openInventory(player)
        }
        Task.everyAsync(20, 20) {
            if (players.size >= 2) {
                val first = players.randomPlayer()
                val second = players.randomPlayer()
                GameService.createGame(first, second)
                players.removePlayer(first, second)
                clicks.removePlayer(first, second)
            }
            NpcService.get(1)!!.fakePlayer.updateName()
        }
    }

}