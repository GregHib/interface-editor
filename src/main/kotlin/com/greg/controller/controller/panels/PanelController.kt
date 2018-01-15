package com.greg.controller.controller.panels

import com.greg.controller.controller.WidgetsController
import com.greg.controller.view.PanelView
import com.greg.ui.panel.Panel
import tornadofx.Controller

class PanelController(val widgets: WidgetsController) : Controller() {

    private val panels = mutableListOf<Panel>()
    lateinit var view: PanelView

    fun reload() {
        panels.forEach { pane ->
            pane.reload(widgets)
        }
    }

    fun addPanel(panel: Panel) {
        panels.add(panel)
    }

    fun get(): MutableList<Panel> {
        return panels
    }
}