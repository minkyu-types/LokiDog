package dev.loki.dog.handler

interface DraggableItem {
    fun onDragStart(index: Int)
    fun onDrag(dragAmount: Float, itemHeight: Float)
    fun onDragEnd()
    fun onDragCancel()
}
