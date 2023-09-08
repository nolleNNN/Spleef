package ru.starfarm.spleef.npcs

import org.bukkit.Location
import ru.starfarm.core.ApiManager
import ru.starfarm.core.entity.impl.FakeVillager
import ru.starfarm.core.util.bukkit.LocationUtil
import ru.starfarm.core.util.format.ChatUtil
import ru.starfarm.spleef.DatabaseConnection
import ru.starfarm.spleef.Logger
import ru.starfarm.spleef.lobby.LobbyService

/**
 * @author nolleNNN
 * @Date 02.09.2023
 * @Time 20:43
 */
object NpcService {
    private val npcs = hashMapOf<Int, NpcInfo>()

    init {
        DatabaseConnection
            .newDatabaseQuery("spleef_npcs")
            .selectQuery()
            .executeQueryAsync(DatabaseConnection)
            .thenAccept {
                while (it.next()) {
                    val id = it.getInt("id")
                    val name = it.getString("name")
                    val location = LocationUtil.fromString(it.getString("location"))
                    val fakeVillager = FakeVillager(location)
                    val npcInfo = NpcInfo(name, location, fakeVillager)
                    npcs[id] = npcInfo
                }
                Logger.info("Loaded ${npcs.size} NPCs")
                it.close()
            }
    }

    fun get(id: Int): NpcInfo? = npcs[id]
}


data class NpcInfo(
    private val name: String,
    private val location: Location,
    private val fake: FakeVillager,
) {
    val fakeVillager get() = fake
    private val holo = ApiManager.createHologram(fakeVillager.location.clone().add(.0, 2.25, .0)).apply {
        textLine(0, "§7Сейчас в очереди: §b${LobbyService.size} §7игроков")
        textLine(1, ChatUtil.color(name))
    }

    fun update() = holo.textLine(0, "§7Сейчас в очереди: §b${LobbyService.size} §7игроков")
}