package ru.starfarm.spleef.listeners

import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import ru.starfarm.spleef.player.SpleefPlayerService

/**
 * @author nolleNNN
 * @Date 02.09.2023
 * @Time 21:36
 */
object LoaderListener : Listener {

    @EventHandler
    fun PlayerJoinEvent.handle() {
        joinMessage = null
        SpleefPlayerService.load(player)
    }

    @EventHandler
    fun PlayerQuitEvent.handle() {
        quitMessage = null
        SpleefPlayerService.unload(player)
    }
}

