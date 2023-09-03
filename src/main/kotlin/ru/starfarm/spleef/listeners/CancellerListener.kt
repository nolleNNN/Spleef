package ru.starfarm.spleef.listeners

import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.CreatureSpawnEvent
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntitySpawnEvent
import org.bukkit.event.entity.FoodLevelChangeEvent
import org.bukkit.event.weather.WeatherChangeEvent

/**
 * @author nolleNNN
 * @Date 03.09.2023
 * @Time 13:36
 */
class CancellerListener : Listener {

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
    fun EntityDamageByEntityEvent.handle() {
        isCancelled = true
    }


}