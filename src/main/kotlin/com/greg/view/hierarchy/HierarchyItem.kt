package com.greg.view.hierarchy

import com.greg.model.widgets.type.Widget
import javafx.scene.control.TreeItem

class HierarchyItem(name: String, val identifier: Int, val widget: Widget) : TreeItem<String>(name) {
    init {
        isExpanded = true
    }
}