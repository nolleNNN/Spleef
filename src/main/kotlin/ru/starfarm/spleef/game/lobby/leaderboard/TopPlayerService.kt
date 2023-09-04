package ru.starfarm.spleef.game.lobby.leaderboard

import ru.starfarm.core.util.bukkit.LocationUtil
import ru.starfarm.spleef.DatabaseConnection

/**
 * @author nolleNNN
 * @Date 04.09.2023
 * @Time 14:10
 */
object TopPlayerService {
    private var PlayersTop: LinkedHashMap<String, TopPlayer>
    private val npcLocations = mutableListOf("world 243 66 295", "world 241 66 295", "world 245 66 295")
        .map { LocationUtil.fromString(it) }.toMutableList()
    private val FirstPlayers get() = PlayersTop.entries.take(3)

    init {
        PlayersTop = top()
    }

    private fun top(): LinkedHashMap<String, TopPlayer> {
        val map = linkedMapOf<String, TopPlayer>()
        DatabaseConnection
            .executeHandler
            .executeQuery(
                false,
                "SELECT * FROM `spleef_players` name NOT IN ('Nollen_') ORDER BY `rating` DESC, `kills` DESC LIMIT 10"
            )
            .thenAccept {
                while (it.next()) {
                    val name = it.getString("name")
                    val rating = it.getInt("rating")
                    val kills = it.getInt("kills")
                    map[name] = TopPlayer(rating, kills)
                }
                it.close()
            }
        return map
    }

    fun updateTop() {
        PlayersTop = top()
    }

}