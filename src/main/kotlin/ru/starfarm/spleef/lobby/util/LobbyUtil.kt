package ru.starfarm.spleef.lobby.util

import org.bukkit.GameMode
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemFlag
import ru.starfarm.core.ApiManager
import ru.starfarm.core.util.bukkit.LocationUtil
import ru.starfarm.map.world.LoadedWorld
import ru.starfarm.spleef.MapService
import ru.starfarm.spleef.player.util.spleefPlayer

/**
 * @author nolleNNN
 * @Date 03.09.2023
 * @Time 14:18
 */
val LobbyLocation = LocationUtil.fromString("world 243 66 290")
val WaitingLobbyLocation = LocationUtil.fromString("world 250 66 300")
val BuyItem = ApiManager.buildItem(Material.PAPER) {
    it.name = "§fПокупка предметов"
    it.addLore(
        "",
        "§aНажмите, чтобы приобрести",
        "§aразличные игровые предметы!"
    )
    it.addItemFlags(*ItemFlag.values())
}

fun Player.moveToLobby() {
    gameMode = GameMode.SURVIVAL
    teleport(LobbyLocation)
    player.spleefPlayer?.removeGameItem()
    addLobbyItem()
}

fun Player.moveToWaitingLobby() {
    teleport(WaitingLobbyLocation)
}

fun Player.addLobbyItem() = player.inventory.addItem(BuyItem)!!

fun Player.removeLobbyItem() = player.inventory.remove(BuyItem)

fun Player.hasItemInMainHand(): Boolean = player.inventory.itemInMainHand == BuyItem
