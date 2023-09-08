package ru.starfarm.spleef.test.stages

import ru.starfarm.spleef.test.Stage

/**
 * @author nolleNNN
 * @Date 07.09.2023
 * @Time 19:00
 */
class GameStage : Stage() {
    override fun startStage() {

    }

    override fun tickStage() {

    }

    override fun endStage(): Stage = GameEndStage()
}