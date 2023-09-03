package ru.starfarm.spleef.player

import org.bukkit.entity.Player
import ru.starfarm.spleef.items.ItemService
import java.util.*

/**
 * @author nolleNNN
 * @Date 02.09.2023
 * @Time 20:57
 */
data class SpleefPlayerInfo(
    val uuid: UUID,
    val player: Player,
    val items: MutableMap<Int, Boolean>,
    var rating: Int = 0,
    var wins: Int = 0,
    var draw: Int = 0,
    var lose: Int = 0,
    var coins: Int = 0
) {
    private val buyItems get() = items.filter { it.value }.keys

    fun addRating(rating: Int) { this.rating += rating }

    fun removeRating(rating: Int) { this.rating -= rating }
    fun addCoin(coins: Int) { this.coins += coins }

    fun removeCoin(coins: Int) { this.coins -= coins }

    fun addWin() { this.wins += 1 }

    fun addDraw() { this.draw += 1 }

    fun addLose() { this.lose += 1 }

    fun buyItem(id: Int) { items[id] = true }

    fun addItem() {
        buyItems.forEach { player.inventory.addItem(ItemService.getItem(it)!!.getBuyItemStack()) }
    }
}