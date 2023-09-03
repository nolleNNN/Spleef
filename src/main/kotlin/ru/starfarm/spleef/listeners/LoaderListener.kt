package ru.starfarm.spleef.listeners

import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.AsyncPlayerPreLoginEvent
import org.bukkit.event.player.PlayerQuitEvent
import ru.starfarm.spleef.player.SpleefPlayerService

/**
 * @author nolleNNN
 * @Date 02.09.2023
 * @Time 21:36
 */
class LoaderListener : Listener {

    @EventHandler
    fun AsyncPlayerPreLoginEvent.handle() {
        SpleefPlayerService.load(uniqueId)
    }

    @EventHandler
    fun PlayerQuitEvent.handle() {
        SpleefPlayerService.unload(player)
    }

}