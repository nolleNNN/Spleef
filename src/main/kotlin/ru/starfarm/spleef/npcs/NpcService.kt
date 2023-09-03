package ru.starfarm.spleef.npcs

import org.bukkit.Location
import ru.starfarm.core.entity.impl.FakePlayer
import ru.starfarm.core.util.bukkit.LocationUtil
import ru.starfarm.core.util.format.ChatUtil
import ru.starfarm.core.util.texture.skin.SkinUtil
import ru.starfarm.spleef.DatabaseConnection
import ru.starfarm.spleef.Logger

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
                    val fakePlayer = FakePlayer(SkinUtil.getSkin(skin)!!, location)
                    val npcInfo = NpcInfo(name, location, skin, fakePlayer)
                    npcs[id] = npcInfo
                }
                Logger.info("Loaded ${npcs.size} NPCs")
            }
    }

    fun get(id: Int): NpcInfo? = npcs[id]
}


data class NpcInfo(
    private val name: String,
    private val location: Location,
    private val skin: String,
    private val fake: FakePlayer,
) {
    val fakePlayer get() = fake

    init {
        fake.apply {
            customName = ChatUtil.color(name)
            customNameVisible = true
        }
    }

}