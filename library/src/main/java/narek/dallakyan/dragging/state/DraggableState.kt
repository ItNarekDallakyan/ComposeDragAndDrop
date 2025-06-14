package narek.dallakyan.dragging.state

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.runtime.withFrameMillis
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.util.fastForEach
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import narek.dallakyan.dragging.animation.DragCancelledAnimation
import narek.dallakyan.dragging.model.ItemPosition
import narek.dallakyan.dragging.model.StartDrag
import kotlin.math.absoluteValue
import kotlin.math.min
import kotlin.math.sign

abstract class DraggableState<T>(
    private val scope: CoroutineScope,
    private val maxScrollPerFrame: Float,
    private val onMove: (fromIndex: ItemPosition, toIndex: ItemPosition) -> Boolean,
    private val canDragOver: ((index: ItemPosition) -> Boolean)?,
    private val onDragEnd: ((startIndex: Int, endIndex: Int) -> Unit)?,
    private val ignoreAnimationsIndexList: MutableList<Int>,
    val dragCancelledAnimation: DragCancelledAnimation
) {
    protected abstract val T.left: Int
    protected abstract val T.top: Int
    protected abstract val T.right: Int
    protected abstract val T.bottom: Int
    protected abstract val T.width: Int
    protected abstract val T.height: Int
    protected abstract val T.itemIndex: Int
    protected abstract val T.itemKey: Any

    abstract val visibleItemsInfo: List<T>
    protected abstract val firstVisibleItemIndex: Int
    protected abstract val firstVisibleItemScrollOffset: Int
    protected abstract val viewportStartOffset: Int
    protected abstract val viewportEndOffset: Int

    val interactions = Channel<StartDrag>()
    val scrollChannel = Channel<Float>()

    val draggingItemLeft: Float
        get() = draggingLayoutInfo?.let { item ->
            (selected?.left ?: 0) + draggingDelta.x - item.left
        } ?: 0f

    val draggingItemTop: Float
        get() = draggingLayoutInfo?.let { item ->
            (selected?.top ?: 0) + draggingDelta.y - item.top
        } ?: 0f

    val draggingItemBottom: Float
        get() = draggingLayoutInfo?.let { item ->
            (selected?.bottom ?: 0) + draggingDelta.y - item.bottom
        } ?: 0f

    var draggingItemIndex by mutableStateOf<Int?>(null)

    val draggingItemKey: Any?
        get() = selected?.itemKey

    private val draggingLayoutInfo: T?
        get() = visibleItemsInfo.firstOrNull { it.itemIndex == draggingItemIndex }

    private var draggingDelta by mutableStateOf(Offset.Zero)
    private var selected by mutableStateOf<T?>(null)
    private var autoscroller: Job? = null
    private val targets = mutableListOf<T>()
    private val distances = mutableListOf<Int>()

    protected abstract suspend fun scrollToItem(index: Int, offset: Int)

    @OptIn(ExperimentalCoroutinesApi::class)
    fun visibleItemsChanged() = snapshotFlow { draggingItemIndex != null }
        .flatMapLatest { if (it) snapshotFlow { visibleItemsInfo } else flowOf(null) }
        .filterNotNull()
        .distinctUntilChanged { old, new ->
            old.firstOrNull()?.itemIndex == new.firstOrNull()?.itemIndex && old.count() == new.count()
        }

    open fun onDragStart(offsetX: Int, offsetY: Int): Boolean {
        return visibleItemsInfo
            .firstOrNull { offsetX in it.left..it.right && offsetY in it.top..it.bottom }
            ?.also {
                selected = it
                draggingItemIndex = it.itemIndex
            } != null
    }

    fun onDragCanceled() {
        val dragIdx = draggingItemIndex
        if (dragIdx != null) {
            val position = ItemPosition(dragIdx, selected?.itemKey)
            val offset = Offset(draggingItemLeft, draggingItemTop)
            if (!ignoreAnimationsIndexList.contains(position.index)) {
                scope.launch {
                    dragCancelledAnimation.dragCancelled(position, offset)
                }
            }
        }

        val startIndex = selected?.itemIndex
        val endIndex = draggingItemIndex

        selected = null
        draggingDelta = Offset.Zero
        draggingItemIndex = null
        cancelAutoScroll()

        onDragEnd?.apply {
            if (startIndex != null && endIndex != null) {
                invoke(startIndex, endIndex)
            }
        }
    }

    fun onDrag(offsetX: Int, offsetY: Int) {
        val selected = selected ?: return
        draggingDelta = Offset(draggingDelta.x + offsetX, draggingDelta.y + offsetY)

        val draggingItem = draggingLayoutInfo ?: return
        val startOffset = draggingItem.top + draggingItemTop
        val startOffsetX = draggingItem.left + draggingItemLeft

        chooseDropItem(
            draggingItem,
            findTargets(draggingDelta.x.toInt(), draggingDelta.y.toInt(), selected),
            startOffsetX.toInt(),
            startOffset.toInt()
        )?.also { targetItem ->
            val from = ItemPosition(draggingItem.itemIndex, draggingItem.itemKey)
            val to = ItemPosition(targetItem.itemIndex, targetItem.itemKey)
            val isBothEquals = onMove.invoke(from, to)

            if (!isBothEquals) {
                draggingItemIndex = targetItem.itemIndex
            }
        }

        calcAutoScrollOffset(0, maxScrollPerFrame).let {
            if (it != 0f) autoscroll(it)
        }
    }

    private fun autoscroll(scrollOffset: Float) {
        if (scrollOffset != 0f) {
            if (autoscroller?.isActive == true) return

            autoscroller = scope.launch {
                var scroll = scrollOffset
                var start = 0L

                while (scroll != 0f && autoscroller?.isActive == true) {
                    withFrameMillis {
                        if (start == 0L) {
                            start = it
                        } else {
                            scroll = calcAutoScrollOffset(it - start, maxScrollPerFrame)
                        }
                    }
                    scrollChannel.trySend(scroll)
                }
            }
        } else {
            cancelAutoScroll()
        }
    }

    private fun cancelAutoScroll() {
        autoscroller?.cancel()
        autoscroller = null
    }

    protected open fun findTargets(x: Int, y: Int, selected: T): List<T> {
        targets.clear()
        distances.clear()

        val left = x + selected.left
        val right = x + selected.right
        val top = y + selected.top
        val bottom = y + selected.bottom
        val centerX = (left + right) / 2
        val centerY = (top + bottom) / 2

        visibleItemsInfo.fastForEach { item ->
            if (item.itemIndex == draggingItemIndex ||
                item.bottom < top ||
                item.top > bottom ||
                item.right < left ||
                item.left > right
            ) {
                return@fastForEach
            }

            if (canDragOver?.invoke(ItemPosition(item.itemIndex, item.itemKey)) != false) {
                val dx = (centerX - (item.left + item.right) / 2).absoluteValue
                val dy = (centerY - (item.top + item.bottom) / 2).absoluteValue
                val dist = dx * dx + dy * dy

                var pos = 0
                for (j in targets.indices) {
                    if (dist > distances[j]) {
                        pos++
                    } else {
                        break
                    }
                }

                targets.add(pos, item)
                distances.add(pos, dist)
            }
        }

        return targets
    }

    protected open fun chooseDropItem(
        draggedItemInfo: T?,
        items: List<T>,
        curX: Int,
        curY: Int
    ): T? {
        if (draggedItemInfo == null) {
            return if (draggingItemIndex != null) items.lastOrNull() else null
        }

        var target: T? = null
        var highScore = -1
        val right = curX + draggedItemInfo.width
        val bottom = curY + draggedItemInfo.height
        val dx = curX - draggedItemInfo.left
        val dy = curY - draggedItemInfo.top

        items.fastForEach { item ->
            if (dx > 0) {
                val diff = item.right - right
                if (diff < 0 && item.right > draggedItemInfo.right) {
                    val score = diff.absoluteValue
                    if (score > highScore) {
                        highScore = score
                        target = item
                    }
                }
            }

            if (dx < 0) {
                val diff = item.left - curX
                if (diff > 0 && item.left < draggedItemInfo.left) {
                    val score = diff.absoluteValue
                    if (score > highScore) {
                        highScore = score
                        target = item
                    }
                }
            }

            if (dy < 0) {
                val diff = item.top - curY
                if (diff > 0 && item.top < draggedItemInfo.top) {
                    val score = diff.absoluteValue
                    if (score > highScore) {
                        highScore = score
                        target = item
                    }
                }
            }

            if (dy > 0) {
                val diff = item.bottom - bottom
                if (diff < 0 && item.bottom > draggedItemInfo.bottom) {
                    val score = diff.absoluteValue
                    if (score > highScore) {
                        highScore = score
                        target = item
                    }
                }
            }
        }

        return target
    }

    private fun calcAutoScrollOffset(time: Long, maxScroll: Float): Float {
        val draggingItem = draggingLayoutInfo ?: return 0f
        val startOffset: Float = draggingItem.top + draggingItemTop
        val endOffset: Float = startOffset + draggingItem.height
        val delta: Float = draggingDelta.y

        val outOfBoundsOffset = when {
            delta > 0 -> (endOffset - viewportEndOffset).coerceAtLeast(0f)
            delta < 0 -> (startOffset - viewportStartOffset).coerceAtMost(0f)
            else -> 0f
        }

        return interpolateOutOfBoundsScroll(
            (endOffset - startOffset).toInt(),
            outOfBoundsOffset,
            time,
            maxScroll
        )
    }

    companion object {
        private const val ACCELERATION_LIMIT_TIME_MS: Long = 1500

        private val EaseOutQuadInterpolator: (Float) -> Float = {
            val t = 1 - it
            1 - t * t * t * t
        }

        private val EaseInQuintInterpolator: (Float) -> Float = {
            it * it * it * it * it
        }

        private fun interpolateOutOfBoundsScroll(
            viewSize: Int,
            viewSizeOutOfBounds: Float,
            time: Long,
            maxScroll: Float
        ): Float {
            if (viewSizeOutOfBounds == 0f) return 0f

            val outOfBoundsRatio = min(1f, 1f * viewSizeOutOfBounds.absoluteValue / viewSize)
            val cappedScroll =
                sign(viewSizeOutOfBounds) * maxScroll * EaseOutQuadInterpolator(outOfBoundsRatio)
            val timeRatio =
                if (time > ACCELERATION_LIMIT_TIME_MS) 1f else time.toFloat() / ACCELERATION_LIMIT_TIME_MS

            return (cappedScroll * EaseInQuintInterpolator(timeRatio)).let {
                if (it == 0f) {
                    if (viewSizeOutOfBounds > 0) 1f else -1f
                } else {
                    it
                }
            }
        }
    }
}