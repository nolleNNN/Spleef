package ru.starfarm.spleef

import org.bukkit.Bukkit
import org.bukkit.event.Listener
import org.bukkit.plugin.java.JavaPlugin
import org.spigotmc.AsyncCatcher
import ru.starfarm.core.CorePlugin
import ru.starfarm.core.database.DatabaseApi
import ru.starfarm.core.database.DatabaseCredentials
import ru.starfarm.core.scoreboard.IScoreboardService
import ru.starfarm.core.scoreboard.ScoreboardService
import ru.starfarm.map.service.IMapService
import ru.starfarm.spleef.commands.SpleefCommand
import ru.starfarm.spleef.commands.SpleefDuelCommand
import ru.starfarm.spleef.commands.SpleefStatsCommand
import ru.starfarm.spleef.items.ItemService
import ru.starfarm.spleef.listeners.CancellerListener
import ru.starfarm.spleef.listeners.LoaderListener
import ru.starfarm.spleef.lobby.LobbyService
import ru.starfarm.spleef.lobby.leaderboard.TopPlayerService
import ru.starfarm.spleef.npcs.NpcService
import ru.starfarm.spleef.player.SpleefPlayerService
import java.util.logging.Logger

/**
 * @author nolleNNN
 * @Date 02.09.2023
 * @Time 20:09
 */
val Plugin: Spleef by lazy { JavaPlugin.getPlugin(Spleef::class.java) }
val Task by lazy(Plugin::taskContext)
val Event by lazy(Plugin::eventContext)
val Logger: Logger by lazy(Plugin::getLogger)
val DatabaseConnection by lazy {
    DatabaseApi.createConnection(
        DatabaseCredentials(
            Plugin.config.getString("mysql.user"),
            Plugin.config.getString("mysql.host"),
            Plugin.config.getString("mysql.password"),
            Plugin.config.getString("mysql.scheme")
        )
    )
}
val Database by lazy { DatabaseConnection.executeHandler }
val MapService by lazy(IMapService::setup)

class Spleef : CorePlugin() {

    override fun enable() {
        AsyncCatcher.enabled = false
        ItemService
        NpcService
        SpleefPlayerService
        LobbyService
        TopPlayerService

        registerListeners(
            LoaderListener, CancellerListener
        )

        registerCommands(
            SpleefCommand, SpleefStatsCommand, SpleefDuelCommand
        )
        registerService(IScoreboardService::class.java, ScoreboardService())
    }

    override fun disable() {
        DatabaseConnection.disconnect()
        Bukkit.getOnlinePlayers().forEach { SpleefPlayerService.unload(it) }
    }

    private fun registerListeners(vararg listeners: Listener) =
        listeners.forEach { Bukkit.getPluginManager().registerEvents(it, this) }
}