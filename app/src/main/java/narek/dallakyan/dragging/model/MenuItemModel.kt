package narek.dallakyan.dragging.model

data class MenuItemModel(
    var title: String,
    var subtitle: String? = null,
    var isEnable: Boolean = true,
    var isClickable: Boolean = false,
    val isLocked: Boolean = false,
) : ItemModel()