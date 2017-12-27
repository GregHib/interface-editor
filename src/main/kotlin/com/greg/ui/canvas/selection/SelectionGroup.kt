package com.greg.ui.canvas.selection

import com.greg.settings.Settings
import com.greg.settings.SettingsKey
import com.greg.ui.canvas.Canvas
import com.greg.ui.canvas.widget.type.types.WidgetGroup

class SelectionGroup(var canvas: Canvas) : WidgetSet() {

    fun toggle(widget: WidgetGroup) {
        if(contains(widget))
            remove(widget)
        else
            add(widget)
    }

    /*
     * Overrides so only change when needed and no unnecessary refreshes
     */
    override fun add(widget: WidgetGroup) {
        if(!contains(widget))
            super.add(widget)
    }

    override fun remove(widget: WidgetGroup) {
        if(contains(widget))
            super.remove(widget)
    }

    override fun handleAddition(widget: WidgetGroup) {
        widget.setSelection(Settings.getColour(SettingsKey.SELECTION_STROKE_COLOUR))
        canvas.refreshSelection()
    }

    override fun handleRemoval(widget: WidgetGroup) {
        widget.setSelection(Settings.getColour(SettingsKey.DEFAULT_STROKE_COLOUR))
        canvas.refreshSelection()
    }
}