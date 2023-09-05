package ru.starfarm.spleef.player

import org.bukkit.entity.Player
import ru.starfarm.spleef.items.ItemService

/**
 * @author nolleNNN
 * @Date 02.09.2023
 * @Time 20:57
 */
data class SpleefPlayerInfo(
    val player: Player,
    val items: MutableMap<Int, Boolean>,
    var rating: Int = 0,
    var wins: Int = 0,
    var draw: Int = 0,
    var lose: Int = 0,
    var coins: Int = 0,
) {
    private val buyItems get() = items.filter { it.value }.keys
    val gameAmount get() = wins + lose + draw
    val percentWin get() = if (lose == 0) 0 else wins / lose

    fun buyItem(id: Int) {
        items[id] = true
    }

    fun addGameItem() = buyItems.forEach { player.inventory.addItem(ItemService.getItem(it)!!.getBuyItemStack()) }
    fun removeGameItem() = buyItems.forEach { player.inventory.remove(ItemService.getItem(it)!!.getBuyItemStack()) }
}
