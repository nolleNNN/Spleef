package ru.starfarm.spleef.player.util

import org.bukkit.Material
import org.bukkit.entity.Player
import ru.starfarm.core.ApiManager
import ru.starfarm.core.util.format.ChatUtil
import ru.starfarm.spleef.player.ISpleefPlayerService

/**
 * @author nolleNNN
 * @Date 03.09.2023
 * @Time 12:55
 */
const val Prefix = "§bSpleef §6> §f"
val Player.profile get() = ApiManager.getPlayerProfile(this)
val Player.coloredName get() = profile!!.coloredName
val Player.spleefPlayer get() = ISpleefPlayerService.get().getSpleefPlayerInfo(this)
val Material.isSpade get() = this == Material.DIAMOND_SPADE
fun Player.sendPlayerMessage(message: String) = sendMessage("$Prefix$message")
fun Player.sendPlayerTitle(upperMessage: String, lowerMessage: String) =
    sendTitle(ChatUtil.color(upperMessage), ChatUtil.color(lowerMessage), 10, 20, 10)

fun MutableSet<Player>.removePlayer(vararg players: Player) = players.forEach { remove(it) }
fun Player.remove(players: HashSet<Player>, clicks: HashSet<Player>) {
    players.remove(this)
    clicks.remove(this)
}

fun Player.add(players: HashSet<Player>, clicks: HashSet<Player>) {
    players.add(this)
    clicks.add(this)
}
