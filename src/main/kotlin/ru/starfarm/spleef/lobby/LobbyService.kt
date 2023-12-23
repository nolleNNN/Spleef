package ru.starfarm.spleef.lobby

import org.bukkit.entity.Player
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent
import ru.starfarm.core.entity.PlayerInteractFakeEntityEvent
import ru.starfarm.core.entity.type.Interact
import ru.starfarm.core.event.on
import ru.starfarm.spleef.Event
import ru.starfarm.spleef.lobby.util.hasItemInMainHand
import ru.starfarm.spleef.menu.ItemBuyMenu
import ru.starfarm.spleef.npcs.NpcService
import ru.starfarm.spleef.player.util.*
import ru.starfarm.spleef.test.Game

/**
 * @author nolleNNN
 * @Date 03.09.2023
 * @Time 14:21
 */
object LobbyService {
    private val clicks = hashSetOf<Player>()
    private val players = hashSetOf<Player>()
    val size get() = players.size

    init {
        Event.on<PlayerInteractFakeEntityEvent> {
            val npc = NpcService.get(1)
            if (interact == Interact.ATTACK) {
                if (entity == npc?.fakeEntity && !clicks.contains(player)) {
                    player.add(players, clicks)
                    player.sendPlayerMessage("§aВы успешно добавлены в очередь!")
                } else {
                    player.remove(players, clicks)
                    player.sendPlayerMessage("§cВы покинули очередь!")
                }
            }
            if (size >= 2) {
                val first = players.first()
                val second = players.last()
                Game(first.spleefPlayer!!, second.spleefPlayer!!)
                players.removePlayer(first, second)
                clicks.removePlayer(first, second)
            }
            npc?.update()
        }
        Event.on<PlayerInteractEvent> {
            if (player.hasItemInMainHand() && (action == Action.RIGHT_CLICK_BLOCK || action == Action.RIGHT_CLICK_AIR))
                ItemBuyMenu().openInventory(player)
        }
    }

}