package ru.starfarm.spleef.player.util

import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import ru.starfarm.core.ApiManager
import ru.starfarm.core.util.format.ChatUtil
import ru.starfarm.spleef.player.SpleefPlayerService
import java.util.concurrent.ThreadLocalRandom

/**
 * @author nolleNNN
 * @Date 03.09.2023
 * @Time 12:55
 */
const val PREFIX = "§bSpleef §6> §f"
val Player.profile get() = ApiManager.getPlayerProfile(this)
val Player.coloredName get() = profile!!.coloredName
val Player.spleefPlayer get() = SpleefPlayerService.getSpleefPlayer(this)
val Material.isSpade get() = this == Material.DIAMOND_SPADE
fun Player.sendPlayerMessage(message: String) = sendMessage("$PREFIX$message")
fun Player.sendPlayerTitle(upperMessage: String, lowerMessage: String) =
    sendTitle(ChatUtil.color(upperMessage), ChatUtil.color(lowerMessage), 10, 20, 10)

fun MutableList<Player>.randomPlayer(): Player = removeAt(ThreadLocalRandom.current().nextInt(size - 1))
fun MutableList<Player>.removePlayer(vararg players: Player) = players.forEach { remove(it) }
fun MutableSet<Player>.removePlayer(vararg players: Player) = players.forEach { remove(it) }
fun String.toPlayer(): Player? = if (Bukkit.getPlayer(this) != null) Bukkit.getPlayer(this) else null
fun Player.remove(players: MutableList<Player>, clicks: MutableSet<Player>) {
    players.remove(this)
    clicks.remove(this)
}

fun Player.add(players: MutableList<Player>, clicks: MutableSet<Player>) {
    players.add(this)
    clicks.add(this)
}
