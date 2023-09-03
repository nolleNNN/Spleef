package ru.starfarm.spleef.game.lobby.util

import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemFlag
import ru.starfarm.core.ApiManager
import ru.starfarm.core.util.bukkit.LocationUtil
import ru.starfarm.core.util.format.ChatUtil

/**
 * @author nolleNNN
 * @Date 03.09.2023
 * @Time 14:18
 */
val LOBBY_LOCATION = LocationUtil.fromString("lobby 1 2 3")
val buyItem = ApiManager.buildItem(Material.PAPER) {
    it.name = ChatUtil.color("§aПокупка предметов")
    it.addLore("", "§aНажмите, чтобы приобрести различные игровые предметы!")
    it.addItemFlags(*ItemFlag.values())
}
fun Player.moveToLobby() {
    this.teleport(LOBBY_LOCATION)
}
fun Player.addItem() {
    player.inventory.addItem(buyItem)
}
fun Player.hasItemInMainHand(): Boolean {
    return player.inventory.itemInMainHand == buyItem
}