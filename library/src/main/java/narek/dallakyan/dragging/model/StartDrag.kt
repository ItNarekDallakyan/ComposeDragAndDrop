package narek.dallakyan.dragging.model

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.PointerId

data class StartDrag(val id: PointerId, val offset: Offset? = null)