package gg.cristalix.spleef.listeners

import gg.cristalix.spleef.player.SpleefPlayerInfo
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import ru.cristalix.core.data.event.PlayerDataLoadEvent

/**
 * @author nolleNNN
 * @Date 02.09.2023
 * @Time 21:36
 */
object LoaderListener : Listener {

    @EventHandler
    fun PlayerDataLoadEvent.handle() {
        val spleefPlayerInfo: SpleefPlayerInfo = if (isFirstJoin)
            SpleefPlayerInfo()
        else
            getDocumentAs(SpleefPlayerInfo::class.java)

        player.setBungeePlayer(spleefPlayerInfo)
    }
}

