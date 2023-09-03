package ru.starfarm.spleef.player.util

import org.bukkit.entity.Player
import ru.starfarm.core.ApiManager
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
fun Player.sendPlayerMessage(message: String) = sendMessage("$PREFIX$message")
fun MutableList<Player>.randomPlayer(): Player = removeAt(ThreadLocalRandom.current().nextInt(size - 1))
