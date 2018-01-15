package com.greg.controller.view

import com.greg.controller.controller.WidgetsController
import com.greg.controller.controller.input.KeyboardController
import com.greg.ui.panel.Panel
import com.greg.ui.panel.panels.PanelType
import javafx.scene.input.KeyEvent
import tornadofx.*

class PanelView : View(), KeyboardController {
    val widgets: WidgetsController by inject()
    val panels = widgets.panels

    init {
        panels.view = this
        panels.addPanel(Panel(PanelType.PROPERTIES))
        panels.addPanel(Panel(PanelType.LAYOUT))
        panels.reload()
    }

    override val root = stackpane {
        prefWidth = 280.0
        prefHeight = 543.0
        scrollpane(fitToWidth = true) {
            squeezebox {
                panels.get().forEach { panel ->
                    fold(panel.type.name.toLowerCase().capitalize(), expanded = true) {
                        isAnimated = false
                        add(panel)
                    }
                }
            }
        }
    }

    override fun handleKeyPress(event: KeyEvent) {
    }

    override fun handleKeyRelease(event: KeyEvent) {
    }
}