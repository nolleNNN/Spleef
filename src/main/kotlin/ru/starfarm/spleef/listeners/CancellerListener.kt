package ru.starfarm.spleef.listeners

import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.entity.*
import org.bukkit.event.player.PlayerDropItemEvent
import org.bukkit.event.weather.WeatherChangeEvent
import ru.starfarm.core.util.item.material
import ru.starfarm.spleef.player.util.isSpade

/**
 * @author nolleNNN
 * @Date 03.09.2023
 * @Time 13:36
 */
object CancellerListener : Listener {

    @EventHandler
    fun WeatherChangeEvent.handle() {
        isCancelled = true
    }

    @EventHandler
    fun FoodLevelChangeEvent.handle() {
        isCancelled = true
    }

    @EventHandler
    fun EntitySpawnEvent.handle() {
        isCancelled = true
    }

    @EventHandler
    fun CreatureSpawnEvent.handle() {
        if (spawnReason != CreatureSpawnEvent.SpawnReason.CUSTOM)
            isCancelled = true
    }

    @EventHandler
    fun EntityDamageEvent.handle() {
        isCancelled = true
    }

    @EventHandler
    fun PlayerDropItemEvent.handle() {
        isCancelled = true
    }

    @EventHandler
    fun BlockBreakEvent.handle() {
        isCancelled = if (player.world.name == "world") true
        else if (!player.inventory.itemInMainHand.material.isSpade) true
        else false
    }

    @EventHandler
    fun BlockPlaceEvent.handle() {
        isCancelled = true
    }

    @EventHandler
    fun EntityDamageByEntityEvent.handle() {
        isCancelled = true
    }


}