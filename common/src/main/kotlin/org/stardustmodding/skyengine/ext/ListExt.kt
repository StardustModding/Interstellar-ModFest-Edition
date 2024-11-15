package org.stardustmodding.skyengine.ext

object ListExt {
    fun <T> MutableList<T>.addIfAbsent(item: T) {
        if (!contains(item)) add(item)
    }

    fun <T> HashSet<T>.addIfAbsent(item: T) {
        if (!contains(item)) add(item)
    }
}
