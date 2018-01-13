package com.greg.ui.hierarchy

import com.greg.ui.canvas.widget.type.types.WidgetGroup
import javafx.scene.control.TreeItem

class CustomTreeItem(val widget: WidgetGroup) : TreeItem<String>(widget.name)