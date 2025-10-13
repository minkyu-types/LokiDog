package dev.loki.dog.feature.detailalarmgroup

object TempIdGenerator {
    private var tempId = -1L
    fun next() = tempId--
}