package narek.dallakyan.dragging.state

import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import narek.dallakyan.dragging.animation.DragCancelledAnimation
import narek.dallakyan.dragging.animation.SpringDragCancelledAnimation
import narek.dallakyan.dragging.model.ItemPosition

/**
 * Remembers a [DraggableLazyListState] with the provided parameters.
 *
 * @param onMove Callback invoked when an item is moved to a new position
 * @param listState The underlying [LazyListState] to use
 * @param canDragOver Optional predicate to determine if an item can be dragged over
 * @param onDragEnd Optional callback invoked when drag operation ends
 * @param maxScrollPerFrame Maximum scroll distance per frame during auto-scroll
 * @param ignoreAnimationsIndexList List of item indices to exclude from drag animations
 * @param dragCancelledAnimation Animation to play when drag is cancelled
 * @return A [DraggableLazyListState] configured with the provided parameters
 */
@Composable
fun rememberRecordableLazyListState(
    onMove: (ItemPosition, ItemPosition) -> Boolean,
    listState: LazyListState = rememberLazyListState(),
    canDragOver: ((index: ItemPosition) -> Boolean)? = null,
    onDragEnd: ((startIndex: Int, endIndex: Int) -> Unit)? = null,
    maxScrollPerFrame: Dp = 20.dp,
    ignoreAnimationsIndexList: MutableList<Int> = mutableListOf(),
    dragCancelledAnimation: DragCancelledAnimation = SpringDragCancelledAnimation()
): DraggableLazyListState {
    val maxScroll = with(LocalDensity.current) { maxScrollPerFrame.toPx() }
    val scope = rememberCoroutineScope()
    val isRtl = LocalLayoutDirection.current == LayoutDirection.Rtl

    val state = remember(listState, ignoreAnimationsIndexList) {
        DraggableLazyListState(
            listState = listState,
            scope = scope,
            maxScrollPerFrame = maxScroll,
            onMove = onMove,
            canDragOver = canDragOver,
            onDragEnd = onDragEnd,
            ignoreAnimationsIndexList = ignoreAnimationsIndexList,
            dragCancelledAnimation = dragCancelledAnimation
        )
    }

    // Trigger drag update when visible items change
    LaunchedEffect(state) {
        state.visibleItemsChanged().collect {
            state.onDrag(0, 0)
        }
    }

    // Handle auto-scrolling during drag
    LaunchedEffect(state) {
        var reverseDirection = !listState.layoutInfo.reverseLayout

        if (isRtl && listState.layoutInfo.orientation != Orientation.Vertical) {
            reverseDirection = !reverseDirection
        }

        val direction = if (reverseDirection) 1f else -1f

        while (true) {
            val diff = state.scrollChannel.receive()
            listState.scrollBy(diff * direction)
        }
    }

    return state
}