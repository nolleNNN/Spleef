package ru.starfarm.spleef.test

import ru.starfarm.spleef.player.SpleefPlayerInfo
import ru.starfarm.spleef.test.stages.LobbyStage

/**
 * @author nolleNNN
 * @Date 15.09.2023
 * @Time 3:39
 */
data class Game(
    val firstPlayer: SpleefPlayerInfo,
    val secondPlayer: SpleefPlayerInfo,
) {
    init {
        LobbyStage(firstPlayer, secondPlayer)
    }
}