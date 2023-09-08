package ru.starfarm.spleef.test

/**
 * @author nolleNNN
 * @Date 07.09.2023
 * @Time 18:57
 */
abstract class Stage {

    abstract fun startStage()
    abstract fun tickStage()
    abstract fun endStage(): Stage?

}