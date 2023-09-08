package ru.starfarm.spleef.items

import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
import org.bukkit.inventory.ItemFlag
import ru.starfarm.core.ApiManager
import ru.starfarm.core.util.format.ChatUtil
import ru.starfarm.spleef.DatabaseConnection
import ru.starfarm.spleef.Logger
import ru.starfarm.spleef.player.util.isSpade

/**
 * @author nolleNNN
 * @Date 02.09.2023
 * @Time 20:21
 */
object ItemService {
    private val items = hashMapOf<Int, ItemInfo>()

    val playerItems = items.values.associate { it.id to false }.toMutableMap()

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
                Logger.info("Loaded ${items.size} items")
                it.close()
            }
    }

    fun getItem(id: Int): ItemInfo? = items[id]

}

data class ItemInfo(
    val id: Int,
    val name: String,
    val lore: String,
    val price: Int,
    val material: Material,
) {

    val itemStack
        get() = ApiManager.buildItem(material) {
            it.name = ChatUtil.color("§f$name")
            for (itemLore in lore.lines()) it.addLore(ChatUtil.color(itemLore))
            it.addLore("", "§bЦена: §6$price §bкоинов.")
            it.addItemFlags(*ItemFlag.values())
        }

    val buyItemStack
        get() = ApiManager.buildItem(material) {
            it.name = ChatUtil.color("§f$name")
            it.addItemFlags(*ItemFlag.values())
            it.unbreakable(true)
            if (material.isSpade) it.enchant(Enchantment.DIG_SPEED, 10)
        }

}