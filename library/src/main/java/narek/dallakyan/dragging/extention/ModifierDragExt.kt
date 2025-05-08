package narek.dallakyan.dragging.extention

import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.input.pointer.pointerInput
import narek.dallakyan.dragging.state.DraggableState

fun Modifier.longPressDraggable(
    state: DraggableState<*>,
    enabled: Boolean = true
) = composed {
    var dragStarted by remember { mutableStateOf(false) }
    DisposableEffect(state) {
        onDispose {
            if (dragStarted) {
                state.onDragCanceled()
                dragStarted = false
            }
        }
    }

    pointerInput(state, enabled) {
        if (enabled) {
            detectDragGesturesAfterLongPress(
                onDragStart = { offset ->
                    dragStarted = true
                    state.onDragStart(offset.x.toInt(), offset.y.toInt())
                },
                onDragEnd = {
                    if (dragStarted) {
                        state.onDragCanceled()
                    }
                    dragStarted = false
                },
                onDragCancel = {
                    if (dragStarted) {
                        state.onDragCanceled()
                    }

                    dragStarted = false
                },
                onDrag = { change, dragAmount ->
                    change.consume()
                    state.onDrag(dragAmount.x.toInt(), dragAmount.y.toInt())
                }
            )
        }
    }
}