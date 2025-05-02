# ComposeDragAndDrop

<div align="center">

![Kotlin](https://img.shields.io/badge/kotlin-%237F52FF.svg?style=for-the-badge&logo=kotlin&logoColor=white)
![Jetpack Compose](https://img.shields.io/badge/Jetpack%20Compose-4285F4.svg?style=for-the-badge&logo=jetpack-compose&logoColor=white)
![Android](https://img.shields.io/badge/Android-3DDC84?style=for-the-badge&logo=android&logoColor=white)
![License](https://img.shields.io/badge/License-Apache_2.0-blue.svg?style=for-the-badge)

A lightweight and customizable Jetpack Compose library for drag-and-drop functionality in vertical and (soon) grid lists.

</div>

## 🚀 Overview

ComposeDragAndDrop provides a flexible and intuitive API for implementing drag-and-drop functionality in Jetpack Compose applications. It offers a modular approach with custom components that seamlessly integrate with LazyColumn and (soon) LazyGrid, allowing developers to create fluid reorderable lists with minimal code.

## ✨ Features

- **Effortless Integration**: Works with standard Compose LazyColumn and LazyGrid components
- **Custom Draggable Modifier**: Simple `.draggable()` modifier to enable drag functionality
- **Flexible Item Customization**: Full control over item appearance during dragging states
- **State Management**: Specialized state handling via `rememberRecordableLazyListState`
- **Conditional Dragging**: Define which items can be dragged with customizable rules
- **Callback System**: Comprehensive callbacks for drag events and reordering operations
- **Performance Optimized**: Designed for smooth performance even with large lists

## 📦 Installation

Since this is an open-source library that you're maintaining, users can include it in their projects using the standard Gradle dependency system.

Add the library to your project by including it in your `build.gradle` file:

```kotlin
dependencies {
    implementation("narek.dallakyan:composedraganddrop:1.0.0")
}
```

Make sure your project has the necessary repositories configured in your project level `build.gradle` or `settings.gradle`:

```kotlin
allprojects {
    repositories {
        google()
        mavenCentral()
        maven("https://plugins.gradle.org/m2/")
    }
}
```

## 🛠️ Usage

### Basic Implementation

```kotlin
@Composable
fun BasicDraggableList(
    viewModel: YourViewModel = viewModel()
) {
    val state = rememberRecordableLazyListState(
        onMove = viewModel::moveItem,
        canDragOver = { true } // Allow all items to be draggable
    )

    LazyColumn(
        modifier = Modifier.draggable(state = state),
        state = state.listState
    ) {
        itemsIndexed(viewModel.items, key = { _, item -> item.id }) { index, item ->
            DraggableItem(
                modifier = Modifier,
                draggableState = state,
                key = item.id,
            ) { isDragging ->
                // Your item content here, with isDragging state to customize appearance
                YourItemComponent(
                    item = item,
                    isDragging = isDragging
                )
            }
        }
    }
}
```

### Advanced Implementation with Custom Sections

```kotlin
@Composable
fun AdvancedDraggableList(
    viewModel: YourViewModel = viewModel()
) {
    val state = rememberRecordableLazyListState(
        onMove = viewModel::moveItem,
        canDragOver = viewModel::isDraggingEnabled // Conditional dragging
    )

    LazyColumn(
        modifier = Modifier.draggable(state = state),
        state = state.listState
    ) {
        // Header section (non-draggable)
        item { 
            ListHeader() 
        }
        
        // Draggable items section
        itemsIndexed(viewModel.items, key = { _, item -> item.id }) { index, item ->
            DraggableItem(
                modifier = Modifier,
                draggableState = state,
                key = item.id,
            ) { isDragging ->
                ItemCard(
                    item = item,
                    isDragging = isDragging,
                    onItemClick = { viewModel.onItemClick(item) }
                )
            }
        }
        
        // Footer section (non-draggable)
        item { 
            ListFooter() 
        }
    }
}
```

### Complete Example with ViewModel

```kotlin
// ViewModel
class DraggableListViewModel : ViewModel() {
    private val _items = mutableStateListOf<YourDataModel>()
    val items: List<YourDataModel> = _items
    
    init {
        // Initialize with your data
        _items.addAll(/* your initial data */)
    }
    
    fun moveItem(fromIndex: Int, toIndex: Int) {
        val item = _items.removeAt(fromIndex)
        _items.add(toIndex, item)
    }
    
    fun isDraggingEnabled(index: Int): Boolean {
        // Add your custom logic here to determine if an item can be dragged
        return true
    }
    
    fun onItemClick(item: YourDataModel) {
        // Handle item click
    }
}

// Composable
@Composable
fun DraggableListScreen(
    viewModel: DraggableListViewModel = viewModel()
) {
    Box(modifier = Modifier.fillMaxSize()) {
        val state = rememberRecordableLazyListState(
            onMove = viewModel::moveItem,
            canDragOver = viewModel::isDraggingEnabled
        )

        LazyColumn(
            modifier = Modifier.draggable(state = state),
            state = state.listState
        ) {
            itemsIndexed(viewModel.items, key = { _, item -> item.id }) { index, item ->
                DraggableItem(
                    modifier = Modifier,
                    draggableState = state,
                    key = item.id,
                ) { isDragging ->
                    YourCustomItem(
                        item = item,
                        isDragging = isDragging,
                        onClick = { viewModel.onItemClick(item) }
                    )
                }
            }
        }
    }
}
```

## 🧩 API Reference

### Core Components

#### `rememberRecordableLazyListState`

Creates and remembers a state object that tracks drag operations.

```kotlin
fun rememberRecordableLazyListState(
    onMove: (Int, Int) -> Unit,
    canDragOver: (Int) -> Boolean = { true }
): RecordableLazyListState
```

| Parameter | Description |
|-----------|-------------|
| onMove | Callback function when items are reordered, with from and to indices |
| canDragOver | Function that determines if an item at a given index can be dragged |

#### `Modifier.draggable`

Modifier that enables drag functionality on a LazyList.

```kotlin
fun Modifier.draggable(
    state: RecordableLazyListState
): Modifier
```

#### `DraggableItem`

Composable that wraps your list item and handles the drag state.

```kotlin
@Composable
fun DraggableItem(
    modifier: Modifier = Modifier,
    draggableState: RecordableLazyListState,
    key: Any,
    content: @Composable BoxScope.(isDragging: Boolean) -> Unit
)
```

## 📱 Use Cases

ComposeDragAndDrop is perfect for:

- To-do and task management apps
- Kanban boards
- Priority lists
- Shopping lists
- Content management systems
- Playlist managers
- Settings and preferences reordering
- Menu builders

## 🧠 How It Works

ComposeDragAndDrop leverages Jetpack Compose's powerful state and gesture systems to provide a seamless drag and drop experience:

1. The `RecordableLazyListState` tracks the current drag operation
2. The `.draggable()` modifier detects and manages drag gestures
3. `DraggableItem` applies visual changes based on drag state
4. When a drag operation completes, the `onMove` callback is triggered to update your data model

## 🛣️ Roadmap

- [ ] Grid layout support
- [ ] Multi-item selection and dragging
- [ ] Drag between different lists
- [ ] Animated placeholder during dragging
- [ ] Enhanced haptic feedback options
- [ ] RTL support
- [ ] Compose Multiplatform support

## 🤝 Contributing

Contributions are welcome! Feel free to open an issue or submit a pull request if you have ideas for improvements or have found a bug.

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add some amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## 📄 License

ComposeDragAndDrop is available under the Apache License, Version 2.0. See the [LICENSE](LICENSE) file for more info.

## 👨‍💻 Author

**Narek Dallakyan** - Senior Android Engineer

[![LinkedIn](https://img.shields.io/badge/LinkedIn-%230077B5.svg?logo=linkedin&logoColor=white)](https://linkedin.com/in/narekdallakyan/)
[![GitHub](https://img.shields.io/badge/GitHub-%23121011.svg?logo=github&logoColor=white)](https://github.com/ItNarekDallakyan)

---

<div align="center">
  Made with ❤️ for the Android community
</div>


## License

```
Copyright 2025 Narek

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
