package ru.starfarm.spleef.test.stages

import ru.starfarm.spleef.test.Stage

/**
 * @author nolleNNN
 * @Date 07.09.2023
 * @Time 18:59
 */
class LobbyStage : Stage() {
    override fun startStage() {

    }

    override fun tickStage() {

    }

    override fun endStage(): Stage = GameStage()
}