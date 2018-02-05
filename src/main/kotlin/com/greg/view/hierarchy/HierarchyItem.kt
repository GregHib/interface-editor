package com.greg.view.hierarchy

import com.greg.model.widgets.Widget
import javafx.scene.control.CheckBoxTreeItem

class HierarchyItem(name: String, val identifier: Int, val widget: Widget) : CheckBoxTreeItem<String>(name)