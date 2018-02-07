package com.greg.view.canvas.states.edit

import com.greg.controller.canvas.PannableCanvas
import com.greg.model.settings.Settings
import com.greg.model.widgets.type.Widget
import com.greg.view.canvas.widgets.WidgetShape
import javafx.scene.input.MouseEvent

class ResizeBox(private val widget: Widget, private val canvas: PannableCanvas) {

    private val points = ResizePoints()

    private var startWidth = widget.getWidth()
    private var startHeight = widget.getWidth()
    var shift = false

    var click: MouseEvent? = null

    fun start(widget: WidgetShape) {
        points.init(widget)
        canvas.children.addAll(points.get())
    }

    fun press(event: MouseEvent) {
        click = event
        startWidth = widget.getWidth()
        startHeight = widget.getHeight()
    }

    fun getDirection(target: ResizePoint): Directions {
        return Directions.values()[points.indexOf(target)]
    }

    fun reset() {
        click = null
    }

    fun close() {
        for (point in points.get())
            canvas.children.remove(point)
        points.close()
    }

    fun resize(direction: Direction, event: MouseEvent) {
        //If is horizontal aka x not y
        val horizontal = direction == Direction.EAST || direction == Direction.WEST

        //If is the top left corner
        val nwCorner = direction == Direction.NORTH || direction == Direction.WEST

        //Get value returned from resize calculations
        calculateResizePosition(horizontal, nwCorner, event)
    }

    private fun calculateResizePosition(horizontal: Boolean, firstHalf: Boolean, mouseEvent: MouseEvent) {
        // Prep
        //---------------------

        //Current mouse position (relative to scene)
        val mousePos = if(horizontal) mouseEvent.sceneX else mouseEvent.sceneY

        //Mouse position of first click (relative to scene)
        val startMousePos = if(horizontal) widget.dragContext.mouseAnchorX else widget.dragContext.mouseAnchorY

        //Widget start position (actual x/y coordinates)
        val startPos = if(horizontal) widget.dragContext.anchorX else widget.dragContext.anchorY

        //Start dimensions
        val start = if (horizontal) startWidth else startHeight

        //Minimum size
        val size = if (horizontal) Settings.getInt(Settings.DEFAULT_WIDGET_MINIMUM_WIDTH) else Settings.getInt(Settings.DEFAULT_WIDGET_MINIMUM_HEIGHT)


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

        //Mouse offset = original mouse position - starting mouse position / scaled
        val mouseOffset = (mousePos - startMousePos) / canvas.scale

        //Dimension = starting dimension (+ or -) mouseOffset
        //First half is minus as mouse offset will be negative
        val value = start + (if(firstHalf) -mouseOffset else mouseOffset)

        //Constrain dimension to minimum size
        val dimension = Math.max(value.toInt(), size)


        // Set
        //---------------------

        //North and West sides need changes to x/y
        if (firstHalf) {
            //Position = start position + mouse offset (which is negative) - Constrained to (opposite side) - min size
            val position = Math.min((startPos + mouseOffset).toInt(), (startPos + start) - size)

            //Apply new position
            if (horizontal)
                widget.setX(position)
            else
                widget.setY(position)
        }

        //Apply new dimension
        if (horizontal)
            widget.setWidth(dimension)
        else
            widget.setHeight(dimension)
    }
}