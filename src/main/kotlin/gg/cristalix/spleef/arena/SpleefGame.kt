package gg.cristalix.spleef.arena

import gg.cristalix.spleef.player.SpleefGameEndType
import gg.cristalix.spleef.player.addSpleefGameResult
import ru.cristalix.core.math.V3
import ru.cristalix.core.multiarena.ArenaStatus
import ru.cristalix.core.util.UtilV3
import java.util.*

/**
 * @author nolleNNN
 * @Date 09.05.2025
 * @Time 0:27
 */
class SpleefGame(private val arena: SpleefArena) {
    private val spawnLocations =
        listOf("firstPoint", "secondPoint")
            .mapNotNull { pointName ->
                arena.gameMap.buildWorldState
                    .points[pointName]
                    ?.firstOrNull()
                    ?.v3
                    ?.let { UtilV3.toLocation(it, arena.gameMap.world) }
            }

    private val schematic = arena.gameMap.buildWorldState
        .schematics["floor"]

    fun teleportToSpawn() {
        arena.players.forEachIndexed { index, player ->
            val spawnLocation = spawnLocations.getOrNull(index) ?: spawnLocations.lastOrNull()
            spawnLocation?.let { player.teleport(it) }
        }
    }

    fun isInZone(v3: V3): Boolean = schematic?.isInZone(v3) ?: false

    fun endGame(loserUUID: UUID) {
        arena.players.forEach { player ->
            if (player.uniqueId == loserUUID) player.addSpleefGameResult(SpleefGameEndType.LOSE)
            else player.addSpleefGameResult(SpleefGameEndType.WIN)
        }
        arena.changeStatus(ArenaStatus.TERMINATING)
    }

}