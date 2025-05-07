package narek.dallakyan.dragging.extention

import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.forEachGesture
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.PointerInputScope
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.util.fastFirstOrNull
import narek.dallakyan.dragging.model.StartDrag
import narek.dallakyan.dragging.state.DraggableState


fun Modifier.draggable(state: DraggableState<*>) = then(
    dragGestureHandler(state)
).then(
    longPressDragStarter(state)
)

private fun dragGestureHandler(state: DraggableState<*>): Modifier = Modifier.pointerInput(Unit) {
    forEachGesture {
        handleDragGesture(state)
    }
}

private suspend fun PointerInputScope.handleDragGesture(state: DraggableState<*>) {
    val dragStart = state.interactions.receive()
    val down = awaitPointerEventScope {
        currentEvent.changes.fastFirstOrNull { it.id == dragStart.id }
    }

    if (down != null && state.onDragStart(down.position.x.toInt(), down.position.y.toInt())) {
        processDragMovement(state, dragStart, down)
    }
}

private suspend fun PointerInputScope.processDragMovement(
    state: DraggableState<*>,
    dragStart: StartDrag,
    down: PointerInputChange
) {
    dragStart.offset?.let {
        state.onDrag(it.x.toInt(), it.y.toInt())
    }

    detectDrag(
        down.id,
        onDragEnd = { state.onDragCanceled() },
        onDragCancel = { state.onDragCanceled() },
        onDrag = { change, dragAmount ->
            change.consume()
            state.onDrag(dragAmount.x.toInt(), dragAmount.y.toInt())
        }
    )
}

private fun longPressDragStarter(state: DraggableState<*>): Modifier = Modifier.pointerInput(true) {
    forEachGesture {
        handleLongPressForDrag(state)
    }
}

private suspend fun PointerInputScope.handleLongPressForDrag(state: DraggableState<*>) {
    val down = awaitPointerEventScope {
        awaitFirstDown(requireUnconsumed = false)
    }
    awaitLongPressOrCancellation(down)?.let {
        state.interactions.trySend(StartDrag(down.id))
    }
}