package gg.cristalix.spleef.listeners

import gg.cristalix.spleef.arena.SpleefArena
import org.bukkit.GameMode
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.inventory.ItemStack
import ru.cristalix.core.multiarena.ArenaStatus
import ru.cristalix.core.multiarena.event.PlayerJoinArenaEvent
import ru.cristalix.core.multiarena.event.PlayerLeaveArenaEvent
import ru.cristalix.core.util.UtilV3

/**
 * @author nolleNNN
 * @Date 09.05.2025
 * @Time 0:04
 */
class GameListener(private val spleefArena: SpleefArena) : Listener {
    private val spadeItem = ItemStack(Material.DIAMOND_SPADE)

    @EventHandler
    fun PlayerJoinArenaEvent.handle() {
        if (arena.status == ArenaStatus.STARTED) player.gameMode = GameMode.SPECTATOR
        else {
            equipment(player)
            spleefArena.changeStatus(ArenaStatus.STARTED)
        }
    }

    @EventHandler
    fun PlayerLeaveArenaEvent.handle() {
        spleefArena.endGame(player.uniqueId)
    }

    @EventHandler
    fun PlayerMoveEvent.handle() {
        if (from.blockY == to.blockY) return
        if (!player.isOnline || spleefArena.status != ArenaStatus.STARTED) return

        val v3Location = UtilV3.fromVector(to.toVector())
        if (spleefArena.isInZone(v3Location)) {
            spleefArena.endGame(player.uniqueId)
            player.gameMode = GameMode.SPECTATOR
        }
    }

    private fun equipment(player: Player) {
        spleefArena.teleportToSpawn()
        player.inventory.addItem(spadeItem)
    }
}