package narek.dallakyan.dragging.extention

import androidx.compose.ui.input.pointer.PointerEvent
import androidx.compose.ui.input.pointer.PointerId
import androidx.compose.ui.util.fastFirstOrNull

fun PointerEvent.isPointerUp(pointerId: PointerId) =
    changes.fastFirstOrNull { it.id == pointerId }?.pressed != true