package gg.cristalix.spleef

import gg.cristalix.spleef.arena.SpleefArena
import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin
import ru.cristalix.core.CoreApi
import ru.cristalix.core.data.PlayerDataService
import ru.cristalix.core.data.listener.PlayerChangeConnectionStateListener
import ru.cristalix.core.database.nosql.mongo.MongoDatabase
import ru.cristalix.core.map.MapService
import ru.cristalix.core.multiarena.IBukkitArena
import ru.cristalix.core.multiarena.IMultiArenaService
import ru.cristalix.core.permissions.DonateRoles
import ru.cristalix.core.permissions.IPermissionService
import ru.cristalix.core.permissions.StaffRoles


/**
 * @author nolleNNN
 * @Date 02.09.2023
 * @Time 20:09
 */
class Spleef : JavaPlugin() {

    override fun onEnable() {
        val mongoDatabase = MongoDatabase()
        mongoDatabase.connect("").join()
        val playerDataService = PlayerDataService(mongoDatabase, "SPLEEF_data")
        registerService()
        applyStaffPermissions(StaffRoles.DEVELOPER, StaffRoles.ADMIN, StaffRoles.OWNER)
        Bukkit.getPluginManager().registerEvents(PlayerChangeConnectionStateListener(playerDataService), this)

        val arenaController = IMultiArenaService.get().getArenaController<IBukkitArena>()
        arenaController?.setCreateHandler("Spleef") { SpleefArena(this, it) }
    }

    private fun registerService() {
        CoreApi.get().registerService(MapService::class.java, MapService())
    }

    private fun applyStaffPermissions(vararg strings: String) {
        val permissionService = IPermissionService.get()
        permissionService.enableTablePermissions()
        for (string in strings) {
            val permissions = permissionService.getGroup(string).permissions
            permissions.clear()

            permissions.addAll(permissionService.getGroup(DonateRoles.GOD).permissions)
            permissions.addAll(permissionService.getGroup(StaffRoles.CURATOR).permissions)

            permissions.add("*")
        }
    }
}