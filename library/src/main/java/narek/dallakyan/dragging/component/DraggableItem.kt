package narek.dallakyan.dragging.component

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.zIndex
import narek.dallakyan.dragging.state.DraggableState

/**
 * A draggable item composable for use in LazyColumn or LazyRow.
 * This version uses animateItemPlacement for smooth animations when items are reordered.
 *
 * @param draggableState State controlling drag behavior
 * @param key Unique identifier for this item
 * @param modifier Modifier to be applied to the item
 * @param index Optional index of the item, used as an alternative to key for identification
 * @param orientationLocked Whether dragging is locked to a single orientation
 * @param content Content to be displayed inside the draggable item
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun LazyItemScope.DraggableItem(
    draggableState: DraggableState<*>,
    key: Any?,
    modifier: Modifier = Modifier,
    index: Int? = null,
    orientationLocked: Boolean = true,
    content: @Composable BoxScope.(isDragging: Boolean) -> Unit
) = DraggableItem(
    draggableState,
    key,
    modifier,
    Modifier.animateItemPlacement(),
    orientationLocked,
    index,
    content
)

/**
 * Base implementation of draggable item that can be used in any container.
 *
 * @param state State controlling drag behavior
 * @param key Unique identifier for this item
 * @param modifier Modifier to be applied to the item
 * @param defaultDraggingModifier Modifier to be applied when item is not being dragged
 * @param orientationLocked Whether dragging is locked to a single orientation
 * @param index Optional index of the item, used as an alternative to key for identification
 * @param content Content to be displayed inside the draggable item
 */
@Composable
fun DraggableItem(
    state: DraggableState<*>,
    key: Any?,
    modifier: Modifier = Modifier,
    defaultDraggingModifier: Modifier = Modifier,
    orientationLocked: Boolean = true,
    index: Int? = null,
    content: @Composable BoxScope.(isDragging: Boolean) -> Unit
) {
    // Determine if this item is being dragged
    val isDragging = if (index != null) {
        index == state.draggingItemIndex
    } else {
        key == state.draggingItemKey
    }

    // Apply different modifiers based on item state
    val draggingModifier = when {
        // Case 1: Item is currently being dragged
        isDragging -> Modifier
            .zIndex(1f)
            .graphicsLayer {
                translationX = calculateTranslationX(
                    state = state,
                    orientationLocked = orientationLocked
                )
                translationY = state.draggingItemTop
            }

        // Case 2: Item is in cancel animation state
        (index != null && index == state.dragCancelledAnimation.position?.index) ||
                (key == state.dragCancelledAnimation.position?.key) -> Modifier
            .zIndex(1f)
            .graphicsLayer {
                translationX = if (!orientationLocked) state.dragCancelledAnimation.offset.x else 0f
                translationY = state.dragCancelledAnimation.offset.y
            }

        // Case 3: Item is in normal state
        else -> defaultDraggingModifier
    }

    // Render the item
    Box(modifier = modifier.then(draggingModifier)) {
        content(isDragging)
    }
}

/**
 * Calculates the horizontal translation for a dragged item.
 * Returns 0 if orientation is locked (vertical-only drag).
 *
 * @param state The drag state
 * @param orientationLocked Whether dragging is locked to vertical only
 * @return The horizontal translation value
 */
private fun calculateTranslationX(
    state: DraggableState<*>,
    orientationLocked: Boolean
) = if (!orientationLocked) state.draggingItemLeft else 0f