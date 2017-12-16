package com.greg.canvas.state.edit.resize

import com.greg.Utils
import com.greg.canvas.WidgetCanvas
import com.greg.canvas.widget.Widget
import com.greg.settings.Settings
import com.greg.settings.SettingsKey
import javafx.geometry.Bounds
import javafx.scene.input.MouseEvent
import javafx.scene.shape.Rectangle

class ResizeController(val canvas: WidgetCanvas, val widget: Widget) {

    private val tabs = TabHandler()

    private val rect = widget.getRectangle().getNode() as Rectangle

    var startWidth = rect.layoutBounds.width
    var startHeight = rect.layoutBounds.height

    var click: MouseEvent? = null

    fun start(widget: Widget) {
        tabs.init(widget)
        canvas.canvasPane.children.addAll(tabs.get())
    }

    fun press(event: MouseEvent) {
        click = event
        val rect = widget.getRectangle().getNode() as Rectangle
        startWidth = rect.width
        startHeight = rect.height
    }

    fun getDirection(target: ResizeTab): ResizeDirection {
        return ResizeDirection.values()[tabs.indexOf(target)]
    }

    fun reset() {
        click = null
    }

    fun close() {
        for (tab in tabs.get())
            canvas.canvasPane.children.remove(tab)
        tabs.close()
    }

    fun resize(direction: Direction, event: MouseEvent, bounds: Bounds) {
        //If is horizontal aka x not y
        val horizontal = direction == Direction.EAST || direction == Direction.WEST

        //If is the top left corner
        val nwCorner = direction == Direction.NORTH || direction == Direction.WEST

        //Get value returned from resize calculations
        val value = calculateResizePosition(horizontal, nwCorner, event, bounds)

        //Apply to layout (only needed for nw corner as se corner only changes width/height)
        if(nwCorner) {
            if(horizontal)
                widget.layoutX = value
            else
                widget.layoutY = value
        }
    }

    private fun calculateResizePosition(horizontal: Boolean, firstHalf: Boolean, mouseEvent: MouseEvent, bounds: Bounds): Double {
        // Prep
        //---------------------

        //Current mouse position
        val event = if(horizontal) mouseEvent.x else mouseEvent.y

        //Offset between original mouse click and widget top left corner
        val offset = if(horizontal) widget.drag?.offsetX!! else widget.drag?.offsetY!!

        //Start click position
        val click = if(horizontal) click?.x!! else click?.y!!

        //Start dimensions
        val start = if(horizontal) startWidth else startHeight

        //Tab size
        val size = if(horizontal) Settings.getDouble(SettingsKey.DEFAULT_WIDGET_MINIMUM_WIDTH) else Settings.getDouble(SettingsKey.DEFAULT_WIDGET_MINIMUM_HEIGHT)

        //Maximum bounds
        val bound = if(horizontal) bounds.width else bounds.height


        var value: Double
        var dimension: Double

        // Calculate
        //---------------------

        //
        //                    x, y
        // Resize nw corner  -->|-------|
        // changes x, y and     |       | height
        // width, height        |       |
        //                      |_______|<--    Resize se corner
        //                        width         only width/height
        //                                      needs changing
        //

        if(firstHalf) {
            //Current position of the side being moved
            var side = event + offset
            //Opposite side position = side being moved's start position + original size
            val opposite = (click + offset) + start

            //The maximum position = opposite side + a minimum size (just used tab size for now)
            val max = opposite - size

            //Constrain the position to the canvas and it's minimum size
            val position = side
            value = Utils.constrain(position, max)//Min is 0 by default

            //New width/height = opposite side - current side position
            dimension = opposite - value
        } else {
            //Offset is distance between original click and n/w side
            //So add start width/height to get actual offset
            var actualOffset = start + offset

            //Current side = current mouse position + actual offset
            val side = actualOffset + event

            //Opposite side = original side + a minimum size (tab size used again for now)
            val opposite = (click + offset) + size

            //Constrain to canvas and self
            value = Utils.constrain(side, opposite, bound)

            //New width/height = original size + difference between original side and current side position (correcting for mouse offset ofc)
            dimension = start + (value - actualOffset - click)
        }

        // Set
        //---------------------

        if (horizontal)
            widget.setWidth(dimension)
        else
            widget.setHeight(dimension)

        return value
    }
}