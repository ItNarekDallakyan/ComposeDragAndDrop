package narek.dallakyan.dragging.animation

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.VectorConverter
import androidx.compose.animation.core.VisibilityThreshold
import androidx.compose.animation.core.spring
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import narek.dallakyan.dragging.model.ItemPosition

/**
 * Interface defining the animation to be played when a drag operation is cancelled.
 */
interface DragCancelledAnimation {
    /**
     * Starts the animation for when drag is cancelled.
     *
     * @param position The position information of the item being dragged
     * @param offset The current offset of the dragged item
     */
    suspend fun dragCancelled(position: ItemPosition, offset: Offset)

    /**
     * The current position information of the item being animated.
     * Will be null when no animation is in progress.
     */
    val position: ItemPosition?

    /**
     * The current offset of the item being animated.
     */
    val offset: Offset
}

/**
 * Implementation of [DragCancelledAnimation] that uses a spring animation
 * to move the dragged item back to its original position.
 *
 * @param stiffness The stiffness of the spring animation. Higher values make the animation faster.
 */
class SpringDragCancelledAnimation(private val stiffness: Float = Spring.StiffnessMediumLow) :
    DragCancelledAnimation {

    private val animatable = Animatable(Offset.Zero, Offset.VectorConverter)

    override val offset: Offset
        get() = animatable.value

    override var position by mutableStateOf<ItemPosition?>(null)
        private set

    override suspend fun dragCancelled(position: ItemPosition, offset: Offset) {
        this.position = position
        animatable.snapTo(offset)

        animatable.animateTo(
            targetValue = Offset.Zero,
            animationSpec = spring(
                stiffness = stiffness,
                visibilityThreshold = Offset.VisibilityThreshold
            )
        )

        this.position = null
    }
}