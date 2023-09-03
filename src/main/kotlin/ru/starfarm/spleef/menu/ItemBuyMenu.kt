package ru.starfarm.spleef.menu

import org.bukkit.entity.Player
import org.bukkit.inventory.ItemFlag
import ru.starfarm.core.ApiManager
import ru.starfarm.core.inventory.container.InventoryContainer
import ru.starfarm.spleef.items.ItemService
import ru.starfarm.spleef.player.util.sendPlayerMessage
import ru.starfarm.spleef.player.util.spleefPlayer

/**
 * @author nolleNNN
 * @Date 03.09.2023
 * @Time 14:44
 */
class ItemBuyMenu : InventoryContainer("Покупка предметов", 3) {
    override fun drawInventory(player: Player) {
        val spleefPlayer = player.spleefPlayer!!

        spleefPlayer.items.forEach { (id, has) ->
            val item = ItemService.getItem(id)!!

            val buyItem = if (has) ApiManager.buildItem(item.material) {
                it.name = item.name
                it.addLore("", "§aВы успешно приобрели данный предмет!")
                it.addItemFlags(*ItemFlag.values())
            } else item.getItemStack()

            addItem(id, buyItem) { _, _ ->
                if (has) {
                    player.sendPlayerMessage("§cУ вас уже есть этот предмет!")
                    return@addItem
                }
                if (spleefPlayer.coins < item.price) {
                    player.sendPlayerMessage("§cУ вас недостаточно средств для покупки этого предмета!")
                    return@addItem
                }
                spleefPlayer.buyItem(id)
                spleefPlayer.removeCoin(item.price)
                player.sendPlayerMessage("§aВы успешно приобрели данный предмет!")
            }
        }
    }
}