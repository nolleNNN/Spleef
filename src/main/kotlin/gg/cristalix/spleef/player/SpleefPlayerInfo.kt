package gg.cristalix.spleef.player

import org.bukkit.entity.Player

/**
 * @author nolleNNN
 * @Date 02.09.2023
 * @Time 20:57
 */
data class SpleefPlayerInfo(
    var rating: Int = 0,
    var game: Int = 0,
    var wins: Int = 0,
    var coins: Int = 0,
) {

    fun addGame(spleefGameEndType: SpleefGameEndType) {
        when (spleefGameEndType) {
            SpleefGameEndType.WIN -> {
                wins++
                game++
            }
            SpleefGameEndType.LOSE -> game++
        }
    }
}


enum class SpleefGameEndType {
    WIN, LOSE
}

fun Player.addSpleefGameResult(gameEndType: SpleefGameEndType) =
    getBungeePlayer<SpleefPlayerInfo>().addGame(gameEndType)
