package narek.dallakyan.dragging.extention

import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.forEachGesture
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.util.fastFirstOrNull
import narek.dallakyan.dragging.model.StartDrag
import narek.dallakyan.dragging.state.DraggableState


fun Modifier.draggable(
    state: DraggableState<*>,
) = then(Modifier.pointerInput(true) {
    forEachGesture {
        val dragStart = state.interactions.receive()
        val down = awaitPointerEventScope {
            currentEvent.changes.fastFirstOrNull { it.id == dragStart.id }
        }
        if (down != null && state.onDragStart(
                down.position.x.toInt(),
                down.position.y.toInt()
            )
        ) {
            dragStart.offset?.apply {
                state.onDrag(x.toInt(), y.toInt())
            }
            detectDrag(
                down.id,
                onDragEnd = {
                    state.onDragCanceled()
                },
                onDragCancel = {
                    state.onDragCanceled()
                },
                onDrag = { change, dragAmount ->
                    change.consume()
                    state.onDrag(dragAmount.x.toInt(), dragAmount.y.toInt())
                }
            )
        }
    }
}).then(
    pointerInput(true) {
        forEachGesture {
            val down = awaitPointerEventScope {
                awaitFirstDown(requireUnconsumed = false)
            }
            awaitLongPressOrCancellation(down)?.also {
                state.interactions.trySend(StartDrag(down.id))
            }
        }
    }
)