import io.nshusa.rsam.util.HashUtils

object HashChecker {
    fun check(name: String, hash: Int): Boolean {
        return HashUtils.nameToHash(name) == hash
    }
}

fun main(args: Array<String>) {

    val list = arrayListOf("anvils", "smithing", "toys", "ATTACK", "DEFENCE", "STRENGTH", "CONSTITUTION", "RANGED", "PRAYER", "MAGIC", "COOKING", "WOODCUTTING", "FLETCHING", "FISHING", "FIREMAKING", "CRAFTING", "SMITHING", "MINING", "HERBLORE", "AGILITY", "THIEVING", "SLAYER", "FARMING", "RUNECRAFTING", "HUNTER", "CONSTRUCTION", "SUMMONING", "DUNGEONEERING",
            "training", "learning", "styles", "hits", "examples", "types", "defences", "defending", "blocks", "start", "finish", "multiple", "rotation", "rotate", "directions", "face", "facing", "way", "turned", "1", "2", "3", "sprites", "man", "men", "small", "0", "little", "gnomes", "attacks", "combats", "fights", "stances", "punches", "icons", "emotes", "off", "on", "miscellaneous", "graphics", "magics")
    val hash = 22834782
    //Individually

    list
            .filter { HashChecker.check("$it.dat", hash) }
            .forEach { println("Success!") }

    //Together
    for (first in list)
        list
                .filter { HashChecker.check("$first$it.dat", hash) }
                .forEach { println("Success!") }

    //Underscore
    for (first in list)
        list
                .filter { HashChecker.check("${first}_$it.dat", hash) }
                .forEach { println("Success!") }


    //Single
    for (first in list) {
        for (f in 0 until first.length) {
            first.substring(0, f)
        }
    }

    //Together

    for (first in list) {
        for (second in list) {
            for (s in 0..second.length) {
                (0..first.length)
                        .filter { HashChecker.check("${second.substring(0, s)}${first.substring(0, it)}.dat", hash) }
                        .forEach { println("Success!") }
            }
        }
    }

    //Underscore
    for (first in list) {
        for (second in list) {
            for (s in 0..second.length) {
                (0..first.length)
                        .filter { HashChecker.check("${second.substring(0, s)}_${first.substring(0, it)}.dat", hash) }
                        .forEach { println("Success!") }
            }
        }
    }
    println("Check complete.")
}