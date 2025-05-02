package narek.dallakyan.dragging.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.ViewModel
import narek.dallakyan.dragging.model.ItemPosition
import narek.dallakyan.dragging.model.MenuItemModel

class FixedViewModel : ViewModel() {

    private val items = MutableList(30) { MenuItemModel(title = "Title $it") }
    private val _menuItems = mutableStateListOf<MenuItemModel>().apply {
        swapList(items)
    }
    var menuItems: SnapshotStateList<MenuItemModel> = _menuItems

    fun isDraggingEnabled(pos: ItemPosition) = menuItems.getOrNull(pos.index)?.isLocked != true

    fun moveMenuItem(from: ItemPosition, to: ItemPosition): Boolean {
        val toIndex = when {
            to.index > menuItems.lastIndex -> menuItems.lastIndex
            to.index - 1 < 0 -> 0
            else -> to.index - 1
        }
        val fromIndex = when {
            from.index > menuItems.lastIndex -> menuItems.lastIndex
            from.index - 1 < 0 -> 0
            else -> from.index - 1
        }
        val isBothEquals = toIndex == fromIndex
        if (isBothEquals.not()) {
            menuItems.move(fromIndex = fromIndex, toIndex = toIndex)
        }
        return isBothEquals
    }
}

fun <T> SnapshotStateList<T>.swapList(newList: List<T>) {
    clear()
    addAll(newList)
}

fun <T> SnapshotStateList<T>.move(fromIndex: Int, toIndex: Int) {
    add(toIndex, removeAt(fromIndex))
}