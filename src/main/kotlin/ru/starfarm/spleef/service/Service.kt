package ru.starfarm.spleef.service

import ru.starfarm.core.util.cast
import ru.starfarm.spleef.player.ISpleefPlayerService
import ru.starfarm.spleef.player.SpleefPlayerService
import java.util.concurrent.ConcurrentHashMap

interface IService

abstract class Service {

    open fun enable() {}

    open fun disable() {}
}

@Suppress("UNCHECKED_CAST")
object ServiceManager {
    private val services = ConcurrentHashMap<Class<out IService>, IService>()

    init {
        registerService(ISpleefPlayerService::class.java, SpleefPlayerService())
    }

    fun unregisterAllServices() = services.keys.onEach { disableService(it) }.forEach { services.remove(it) }

    fun <S : IService> getService(type: Class<out S>): S = services[type]!! as S

    private fun <S : IService> registerService(type: Class<out S>, service: S): S {
        services[type] = service

        return enableService(type)
    }

    private fun <S : IService> enableService(type: Class<out S>): S {
        val service = getService(type)
        service.cast<Service>().enable()
        return service
    }

    private fun <S : IService> disableService(type: Class<out S>): S {
        val service = getService(type)
        service.cast<Service>().disable()
        return service
    }
}