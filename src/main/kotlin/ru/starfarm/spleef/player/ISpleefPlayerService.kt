package ru.starfarm.spleef.player

import org.bukkit.entity.Player
import ru.starfarm.core.util.cast
import ru.starfarm.spleef.service.IService
import ru.starfarm.spleef.service.ServiceManager
import java.util.UUID

interface ISpleefPlayerService : IService {

    companion object {
        fun get() : SpleefPlayerService {
            return ServiceManager.getService(ISpleefPlayerService::class.java).cast()
        }
    }
    fun load(uuid: UUID)

    fun load(player: Player)

    fun unload(uuid: UUID)

    fun unload(player: Player)

    fun save(spleefPlayerInfo: SpleefPlayerInfo)

    fun saveAllPlayers()

    fun getSpleefPlayerInfo(uuid: UUID): SpleefPlayerInfo?

    fun getSpleefPlayerInfo(name: String): SpleefPlayerInfo?

    fun getSpleefPlayerInfo(player: Player): SpleefPlayerInfo?

}