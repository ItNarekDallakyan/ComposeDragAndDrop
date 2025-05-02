package narek.dallakyan.dragging.state

import androidx.compose.foundation.lazy.LazyListItemInfo
import androidx.compose.foundation.lazy.LazyListState
import kotlinx.coroutines.CoroutineScope
import narek.dallakyan.dragging.animation.DragCancelledAnimation
import narek.dallakyan.dragging.animation.SpringDragCancelledAnimation
import narek.dallakyan.dragging.model.ItemPosition

/**
 * Implementation of [DraggableState] for LazyList components.
 */
class DraggableLazyListState(
    val listState: LazyListState,
    scope: CoroutineScope,
    maxScrollPerFrame: Float,
    onMove: (fromIndex: ItemPosition, toIndex: ItemPosition) -> Boolean,
    canDragOver: ((index: ItemPosition) -> Boolean)? = null,
    onDragEnd: ((startIndex: Int, endIndex: Int) -> Unit)? = null,
    ignoreAnimationsIndexList: MutableList<Int> = mutableListOf(),
    dragCancelledAnimation: DragCancelledAnimation = SpringDragCancelledAnimation()
) : DraggableState<LazyListItemInfo>(
    scope,
    maxScrollPerFrame,
    onMove,
    canDragOver,
    onDragEnd,
    ignoreAnimationsIndexList,
    dragCancelledAnimation
) {
    override val LazyListItemInfo.left: Int
        get() = if (listState.layoutInfo.reverseLayout)
            listState.layoutInfo.viewportSize.width - offset - size
        else 0

    override val LazyListItemInfo.top: Int
        get() = if (listState.layoutInfo.reverseLayout)
            listState.layoutInfo.viewportSize.height - offset - size
        else offset

    override val LazyListItemInfo.right: Int
        get() = if (listState.layoutInfo.reverseLayout)
            listState.layoutInfo.viewportSize.width - offset
        else 0

    override val LazyListItemInfo.bottom: Int
        get() = if (listState.layoutInfo.reverseLayout)
            listState.layoutInfo.viewportSize.height - offset
        else offset + size

    override val LazyListItemInfo.width: Int get() = 0
    override val LazyListItemInfo.height: Int get() = size
    override val LazyListItemInfo.itemIndex: Int get() = index
    override val LazyListItemInfo.itemKey: Any get() = key

    override val visibleItemsInfo: List<LazyListItemInfo>
        get() = listState.layoutInfo.visibleItemsInfo

    override val viewportStartOffset: Int
        get() = listState.layoutInfo.viewportStartOffset

    override val viewportEndOffset: Int
        get() = listState.layoutInfo.viewportEndOffset

    override val firstVisibleItemIndex: Int
        get() = listState.firstVisibleItemIndex

    override val firstVisibleItemScrollOffset: Int
        get() = listState.firstVisibleItemScrollOffset

    override suspend fun scrollToItem(index: Int, offset: Int) {
        listState.animateScrollToItem(index, offset)
    }

    override fun onDragStart(offsetX: Int, offsetY: Int): Boolean =
        super.onDragStart(0, offsetY)

    override fun findTargets(x: Int, y: Int, selected: LazyListItemInfo) =
        super.findTargets(0, y, selected)

    override fun chooseDropItem(
        draggedItemInfo: LazyListItemInfo?,
        items: List<LazyListItemInfo>,
        curX: Int,
        curY: Int
    ) = super.chooseDropItem(draggedItemInfo, items, 0, curY)
}