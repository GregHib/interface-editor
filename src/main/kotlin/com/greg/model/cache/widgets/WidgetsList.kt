package com.greg.model.cache.widgets

import com.greg.controller.cache.CacheController
import com.greg.controller.widgets.WidgetsController
import com.greg.model.settings.Settings
import com.greg.view.sprites.SpriteController
import io.nshusa.rsam.binary.Widget
import javafx.scene.control.SelectionMode
import javafx.scene.control.TableView
import tornadofx.*

class WidgetsList : Fragment("Select a Widget") {

    val cache: CacheController by inject()
    val widgets: WidgetsController by inject()
    val sprites: SpriteController by inject()

    lateinit var list: TableView<Widget>

    override val root = borderpane {
        list = tableview(CacheController.widgets) {
            selectionModel.selectionMode = SelectionMode.SINGLE
            column("ID", Widget::id) {
                maxWidth = 50.0
            }
            val c = column("Widget", Widget::toBufferedImage)
            c.minWidth = Settings.getDouble(Settings.DEFAULT_WIDGET_LIST_IMAGE_WIDTH)
            c.setCellFactory { WidgetListCell() }
        }
        center = list
        list.onDoubleClick {
            widgets.open(list.selectionModel.selectedItem, sprites)
            println("Open interface: ${list.selectionModel.selectedItem.id}")
        }
    }

    init {
        cache.loadWidgets()
    }
}