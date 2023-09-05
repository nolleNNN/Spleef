package ru.starfarm.spleef.game.util

import org.bukkit.Bukkit
import ru.starfarm.map.world.LoadedWorld

/**
 * @author nolleNNN
 * @Date 05.09.2023
 * @Time 20:30
 */

fun LoadedWorld.unloadWorld() = Bukkit.unloadWorld(world, false)