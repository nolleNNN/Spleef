package ru.starfarm.spleef.npcs

import org.bukkit.Location
import ru.starfarm.core.entity.impl.FakePlayer
import ru.starfarm.core.util.bukkit.LocationUtil
import ru.starfarm.core.util.format.ChatUtil
import ru.starfarm.core.util.texture.skin.SkinUtil
import ru.starfarm.spleef.DatabaseConnection

/**
 * @author nolleNNN
 * @Date 02.09.2023
 * @Time 20:43
 */
object NpcService {
    private val npcs = mutableMapOf<Int, NpcInfo>()

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
                    val skin = it.getString("skin")
                    val npcInfo = NpcInfo(name, location, skin)
                    npcs[id] = npcInfo
                    npcInfo.createNpc()
                }
            }
    }

    fun getNpc(id: Int): NpcInfo? {
        return npcs[id]
    }

}


data class NpcInfo(
    val name: String,
    val location: Location,
    val skin: String
) {
    fun createNpc() {
        FakePlayer(SkinUtil.getSkin(skin)!!, location).apply {
            customName = ChatUtil.color(name)
            customNameVisible = true
        }
    }


}