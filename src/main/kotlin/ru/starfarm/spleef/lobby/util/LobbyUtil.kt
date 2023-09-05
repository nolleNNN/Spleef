package ru.starfarm.spleef.lobby.util

import org.bukkit.GameMode
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemFlag
import ru.starfarm.core.ApiManager
import ru.starfarm.core.util.bukkit.LocationUtil
import ru.starfarm.spleef.player.util.spleefPlayer

/**
 * @author nolleNNN
 * @Date 03.09.2023
 * @Time 14:18
 */
val LOBBY_LOCATION = LocationUtil.fromString("world 243 66 290")
val buyItem = ApiManager.buildItem(Material.PAPER) {
    it.name = "§fПокупка предметов"
    it.addLore("", "§aНажмите, чтобы приобрести", "§aразличные игровые предметы!")
    it.addItemFlags(*ItemFlag.values())
}

fun Player.moveToLobby() {
    gameMode = GameMode.ADVENTURE
    teleport(LOBBY_LOCATION)
    player.spleefPlayer!!.removeGameItem()
    addLobbyItem()
}

fun Player.addLobbyItem() = player.inventory.addItem(buyItem)!!

fun Player.removeLobbyItem() = player.inventory.remove(buyItem)

fun Player.hasItemInMainHand(): Boolean = player.inventory.itemInMainHand == buyItem
