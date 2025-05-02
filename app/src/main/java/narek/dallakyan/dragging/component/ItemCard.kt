package narek.dallakyan.dragging.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import narek.dallakyan.dragging.model.MenuItemModel

@ExperimentalComposeUiApi
@Composable
fun ItemCard(
    modifier: Modifier = Modifier,
    menuItem: MenuItemModel,
    isDraggingItem: Boolean = false,
) {

    Card(
        modifier = modifier
            .padding(
                top = 8.dp,
                start = if (isDraggingItem) 13.dp else 19.dp,
                end = if (isDraggingItem) 14.dp else 20.dp,
                bottom = 1.dp,
            )
            .height(72.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isDraggingItem) Color(0xFFFFFFFF) else Color(0xFFF7F7F7)
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isDraggingItem) 4.dp else 2.dp
        ),
        border = BorderStroke(width = 1.dp, color = Color(0xFFFFFFFF)),
    ) {
        Row(
            horizontalArrangement = Arrangement.Center,
        ) {
            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(56.dp)
                    .padding(start = 13.dp, end = 11.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Image(
                    modifier = Modifier.scale(if (isDraggingItem) 0.75f else 1f),
                    imageVector = Icons.Default.Menu,
                    contentDescription = "menu item icon",
                )
            }

            HorizontalDivider(
                modifier = Modifier
                    .fillMaxHeight()
                    .padding(top = 12.dp, bottom = 13.dp)
                    .width(1.dp)
                    .alpha(0.5f),
                color = Color(0xFFC6C9D2)
            )

            Row(
                modifier = Modifier
                    .fillMaxHeight()
                    .padding(start = 16.dp, end = 20.dp, top = 13.dp, bottom = 13.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxHeight()
                        .weight(1f),
                    verticalArrangement = Arrangement.Center,
                ) {
                    Text(
                        modifier = Modifier.alpha(if (!menuItem.isEnable || isDraggingItem) 0.65f else 1f),
                        text = menuItem.title,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        color = Color.Black,
                    )
                }
            }
        }
    }
}