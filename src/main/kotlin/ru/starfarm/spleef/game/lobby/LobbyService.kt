package ru.starfarm.spleef.game.lobby

import org.bukkit.entity.Player
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerChangedWorldEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerJoinEvent
import ru.starfarm.core.entity.PlayerInteractFakeEntityEvent
import ru.starfarm.core.event.on
import ru.starfarm.spleef.Event
import ru.starfarm.spleef.Task
import ru.starfarm.spleef.game.Game
import ru.starfarm.spleef.game.lobby.util.addItem
import ru.starfarm.spleef.game.lobby.util.hasItemInMainHand
import ru.starfarm.spleef.menu.ItemBuyMenu
import ru.starfarm.spleef.npcs.NpcService
import ru.starfarm.spleef.player.util.randomPlayer
import ru.starfarm.spleef.player.util.removePlayer
import ru.starfarm.spleef.player.util.sendPlayerMessage

/**
 * @author nolleNNN
 * @Date 03.09.2023
 * @Time 14:21
 */
object LobbyService {
    private val players = mutableListOf<Player>()
    private val clicks = mutableSetOf<Player>()

    init {
        Event.on<PlayerInteractFakeEntityEvent> {
            if (entity == NpcService.get(1)!!.fakePlayer && !clicks.contains(player)) {
                players.add(player)
                clicks.add(player)
                player.sendPlayerMessage("§aВы успешно добавлены в очередь!")
            } else {
                players.remove(player)
                clicks.remove(player)
                player.sendPlayerMessage("§cВы покинули очередь!")
            }
        }
        Event.on<PlayerJoinEvent> { player.addItem() }
        Event.on<PlayerChangedWorldEvent> { if (from.name == "world") player.addItem() }
        Event.on<PlayerInteractEvent> {
            if (player.hasItemInMainHand() && (action == Action.RIGHT_CLICK_BLOCK || action == Action.RIGHT_CLICK_AIR))
                ItemBuyMenu().openInventory(player)
        }
        Task.everyAsync(20, 20) {
            if (players.size >= 2) {
                val first = players.randomPlayer()
                val second = players.randomPlayer()
                clicks.removePlayer(first, second)
                Game().startGame(first, second)
            }
        }
    }


}