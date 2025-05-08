package gg.cristalix.spleef.arena

import gg.cristalix.spleef.listeners.GameListener
import gg.cristalix.spleef.listeners.LoaderListener
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.World
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.entity.EntitySpawnEvent
import org.bukkit.event.entity.FoodLevelChangeEvent
import org.bukkit.event.player.PlayerDropItemEvent
import org.bukkit.event.weather.WeatherChangeEvent
import org.bukkit.plugin.Plugin
import ru.cristalix.core.map.BukkitWorldLoader
import ru.cristalix.core.map.IMapService
import ru.cristalix.core.map.LoadedMap
import ru.cristalix.core.math.V3
import ru.cristalix.core.multiarena.Arena
import ru.cristalix.core.multiarena.ArenaData
import ru.cristalix.core.multiarena.ArenaStatus
import ru.cristalix.core.util.UtilV3
import java.util.*

/**
 * @author nolleNNN
 * @Date 08.05.2025
 * @Time 23:31
 */
class SpleefArena(plugin: Plugin, data: ArenaData) : Arena(plugin, data) {
    private val mapService = IMapService.get()
    lateinit var gameMap: LoadedMap<World>
    private lateinit var spleefGame: SpleefGame

    override fun initialize() {
        loadMap()
        changeStatus(ArenaStatus.INITIALIZATION)
        spleefGame = SpleefGame(this)

        context.subscribe(
            LoaderListener,
            GameListener(this)
        )
        context.subscribe(BlockBreakEvent::class.java) {
            it.isCancelled = it.block.type == Material.SNOW_BLOCK
        }
        context.cancel(WeatherChangeEvent::class.java)
        context.cancel(FoodLevelChangeEvent::class.java)
        context.cancel(EntitySpawnEvent::class.java)
        context.cancel(EntityDamageEvent::class.java)
        context.cancel(PlayerDropItemEvent::class.java)
        context.cancel(BlockPlaceEvent::class.java)
        context.cancel(EntityDamageByEntityEvent::class.java)

    }

    override fun getSpawnLocation(): Location =
        UtilV3.toLocation(
            gameMap
                .buildWorldState
                .points["spawn"]
                ?.first()
                ?.v3,
            gameMap.world
        )

    fun changeStatus(arenaStatus: ArenaStatus) {
        data.status = arenaStatus
    }

    fun isInZone(v3: V3): Boolean = spleefGame.isInZone(v3)

    fun teleportToSpawn() = spleefGame.teleportToSpawn()

    fun endGame(loserUUID: UUID) = spleefGame.endGame(loserUUID)

    private fun loadMap() =
        mapService.getLatestMapByGameTypeAndMapName("Spleef", "GameMap")
            .ifPresent {
                try {
                    gameMap = mapService.loadMap(it.latest, BukkitWorldLoader.INSTANCE).get()
                    addWorld(gameMap.world)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

}