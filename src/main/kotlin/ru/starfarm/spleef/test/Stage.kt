package ru.starfarm.spleef.test

import ru.starfarm.spleef.Event
import ru.starfarm.spleef.Task

/**
 * @author nolleNNN
 * @Date 07.09.2023
 * @Time 18:57
 */
abstract class Stage {
    val taskContext = Task.fork(true)
    val eventContext = Event.fork(true)
    abstract fun startStage()
    abstract fun tickStage()
    abstract fun endStage(): Stage?

}