package io.genanik.picfinder.plugins.messageReapeater

fun <Char> Array<Char>.findOrNull(targetChar: Char): Int? {
    for ((index, i) in this.withIndex()){
        if (i == targetChar){
            return index
        }
    }
    return null
}