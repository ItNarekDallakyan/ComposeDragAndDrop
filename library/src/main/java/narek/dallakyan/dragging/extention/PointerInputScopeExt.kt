package narek.dallakyan.dragging.extention

import androidx.compose.foundation.gestures.drag
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.*
import androidx.compose.ui.util.fastAll
import androidx.compose.ui.util.fastAny
import androidx.compose.ui.util.fastFirstOrNull
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.withTimeout

/**
 * Waits for a long press event or cancellation.
 *
 * @param initialDown The initial pointer down event
 * @return The pointer input change corresponding to the long press, or null if canceled
 */
internal suspend fun PointerInputScope.awaitLongPressOrCancellation(
    initialDown: PointerInputChange
): PointerInputChange? {
    var longPress: PointerInputChange? = null
    var currentDown = initialDown
    val longPressTimeout = viewConfiguration.longPressTimeoutMillis

    return try {
        withTimeout(longPressTimeout) {
            awaitPointerEventScope {
                var finished = false

                while (!finished) {
                    val event = awaitPointerEvent(PointerEventPass.Main)

                    // Check if all pointers are up
                    if (event.changes.fastAll { it.changedToUpIgnoreConsumed() }) {
                        finished = true
                    }

                    // Check if any pointer is consumed or out of bounds
                    if (event.changes.fastAny {
                            it.isConsumed || it.isOutOfBounds(size, extendedTouchPadding)
                        }) {
                        finished = true // Canceled
                    }

                    // Check for position consumption in the Final pass
                    val consumeCheck = awaitPointerEvent(PointerEventPass.Final)
                    if (consumeCheck.changes.fastAny { it.isConsumed }) {
                        finished = true
                    }

                    // Track the current pointer
                    if (!event.isPointerUp(currentDown.id)) {
                        longPress = event.changes.fastFirstOrNull { it.id == currentDown.id }
                    } else {
                        val newPressed = event.changes.fastFirstOrNull { it.pressed }
                        if (newPressed != null) {
                            currentDown = newPressed
                            longPress = currentDown
                        } else {
                            finished = true
                        }
                    }
                }
            }
        }
        null // Return null if the timeout didn't trigger (meaning it was canceled)
    } catch (_: TimeoutCancellationException) {
        // Return the long press event when timeout occurs
        longPress ?: initialDown
    }
}

/**
 * Detects and handles a drag gesture.
 *
 * @param down The pointer ID of the initially pressed pointer
 * @param onDragEnd Called when drag ends normally with an up event
 * @param onDragCancel Called when drag is interrupted
 * @param onDrag Called when drag moves, with the change and the position change
 */
internal suspend fun PointerInputScope.detectDrag(
    pointerId: PointerId,
    onDragEnd: () -> Unit = { },
    onDragCancel: () -> Unit = { },
    onDrag: (change: PointerInputChange, dragAmount: Offset) -> Unit
) {
    awaitPointerEventScope {
        val dragResult = drag(pointerId) {
            onDrag(it, it.positionChange())
            it.consume()
        }

        if (dragResult) {
            // Drag ended gracefully with an up event
            currentEvent.changes.forEach {
                if (it.changedToUp()) it.consume()
            }
            onDragEnd()
        } else {
            // Drag was canceled
            onDragCancel()
        }
    }
}