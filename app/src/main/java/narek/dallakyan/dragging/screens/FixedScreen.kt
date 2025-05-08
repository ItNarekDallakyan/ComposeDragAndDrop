package narek.dallakyan.dragging.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import narek.dallakyan.dragging.component.DraggableItem
import narek.dallakyan.dragging.component.ItemCard
import narek.dallakyan.dragging.extention.longPressDraggable
import narek.dallakyan.dragging.model.MenuItemModel
import narek.dallakyan.dragging.state.rememberRecordableLazyListState
import narek.dallakyan.dragging.viewmodel.FixedViewModel

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun FixedScreen(
    fixedViewModel: FixedViewModel = viewModel(),
) {

    Box(modifier = Modifier.fillMaxSize()) {
        VerticalLazyColumn(
            modifier = Modifier,
            fixedViewModel = fixedViewModel,
            beforeItemsContent = { Text(text = "") },
            itemsIndexedContent = { dragging, _, menuItem ->
                ItemCard(
                    menuItem = menuItem,
                    isDraggingItem = dragging,
                )
            },
            afterItemsContent = { Text(text = "") }
        )
    }
}


@Composable
fun VerticalLazyColumn(
    modifier: Modifier = Modifier,
    fixedViewModel: FixedViewModel,
    itemsIndexedContent: @Composable BoxScope.(isDragging: Boolean, index: Int, item: MenuItemModel) -> Unit,
    beforeItemsContent: @Composable LazyItemScope.() -> Unit,
    afterItemsContent: @Composable LazyItemScope.() -> Unit
) {
    val state = rememberRecordableLazyListState(
        onMove = fixedViewModel::moveMenuItem,
        canDragOver = fixedViewModel::isDraggingEnabled
    )

    LazyColumn(
        modifier = modifier.longPressDraggable(state = state),
        state = state.listState
    ) {
        item { beforeItemsContent.invoke(this) }
        itemsIndexed(fixedViewModel.menuItems, key = { _, item -> item.id }) { index, item ->
            DraggableItem(
                modifier = Modifier,
                draggableState = state,
                key = item.id,
            ) { dragging ->
                itemsIndexedContent.invoke(this, dragging, index, item)
            }
        }
        item { afterItemsContent.invoke(this) }
    }
}