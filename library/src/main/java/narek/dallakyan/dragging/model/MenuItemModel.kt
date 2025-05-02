package narek.dallakyan.dragging.model

import java.util.UUID

data class MenuItemModel(
    var id: String = UUID.randomUUID().toString(),
    var title: String,
    var subtitle: String? = null,
    var isEnable: Boolean = true,
    var isClickable: Boolean = false,
    val isLocked: Boolean = false,
)