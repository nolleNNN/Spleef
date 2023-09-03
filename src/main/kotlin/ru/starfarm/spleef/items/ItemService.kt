package ru.starfarm.spleef.items

import org.bukkit.Material
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import ru.starfarm.core.ApiManager
import ru.starfarm.core.util.format.ChatUtil
import ru.starfarm.spleef.DatabaseConnection

/**
 * @author nolleNNN
 * @Date 02.09.2023
 * @Time 20:21
 */
object ItemService {
    private val items = mutableMapOf<Int, ItemInfo>()

    init {
        DatabaseConnection
            .newDatabaseQuery("spleef_items")
            .selectQuery()
            .executeQueryAsync(DatabaseConnection)
            .thenAccept {
                while (it.next()) {
                    val id = it.getInt("id")
                    val name = it.getString("name")
                    val lore = it.getString("lore")
                    val price = it.getInt("price")
                    val material = Material.getMaterial(it.getString("material"))
                    items[id] = ItemInfo(id, name, lore, price, material)
                }
            }
    }

    fun getItem(id: Int): ItemInfo? {
        return items[id]
    }
}

data class ItemInfo(
    val id: Int,
    val name: String,
    val lore: String,
    val price: Int,
    val material: Material
) {

    fun getItemStack(): ItemStack {
        return ApiManager.buildItem(material) {
            it.name = ChatUtil.color(name)
            it.lore(lore)
            it.addLore("", "§bЦена: §6$price §bкоинов.")
            it.addItemFlags(*ItemFlag.values())
        }
    }

    fun getBuyItemStack(): ItemStack {
        return ApiManager.buildItem(material) {
            it.name = ChatUtil.color(name)
            it.addItemFlags(*ItemFlag.values())
        }
    }

}