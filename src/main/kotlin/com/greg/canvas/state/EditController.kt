package com.greg.canvas.state

import com.greg.canvas.WidgetCanvas
import com.greg.canvas.edit.Stretcher
import com.greg.canvas.widget.Widget
import javafx.scene.Cursor
import javafx.scene.input.MouseEvent
import javafx.scene.paint.Color
import javafx.scene.shape.Path
import javafx.scene.shape.Rectangle
import javafx.scene.shape.Shape
import kotlin.math.round


class EditController(var canvas: WidgetCanvas, var widget: Widget) : PaneController {

    private val path: Shape

    private val x = widget.getNode().layoutX
    private val y = widget.getNode().layoutY
    private val width = widget.getRectangle().getNode().layoutBounds.width
    private val height = widget.getRectangle().getNode().layoutBounds.height

    private val tabs = mutableListOf<Stretcher>()

    init {
        val mask = Rectangle(x, y, width, height)
        val rectangle = Rectangle(765.0, 503.0)
        path = Path.subtract(rectangle, mask)
        path.fill = Color.rgb(0, 0, 155, 0.6)
        canvas.canvasPane.children.add(path)

        val offset = 0

        val size = 8.0
        val halfWidth = width/2.0 - size/2.0
        val halfHeight = height/2.0 - size/2.0

        val left = x + offset
        val right = x + width - size - offset
        val top = y + offset
        val bottom = y + height - size - offset
        val centreX = round(x + halfWidth)
        val centreY = round(y + halfHeight)

        //Top left
        addTab(left, top).addCursor(Cursor.NW_RESIZE)

        //Top
        addTab(centreX, top).addCursor(Cursor.N_RESIZE)

        //Top right
        addTab(right, top).addCursor(Cursor.NE_RESIZE)

        //Left
        addTab(left, centreY).addCursor(Cursor.W_RESIZE)

        //Right
        addTab(right, centreY).addCursor(Cursor.E_RESIZE)

        //Bottom left
        addTab(left, bottom).addCursor(Cursor.SW_RESIZE)

        //Bottom
        addTab(centreX, bottom).addCursor(Cursor.S_RESIZE)

        //Bottom right
        addTab(right, bottom).addCursor(Cursor.SE_RESIZE)
    }

    private fun addTab(x: Double, y: Double): Stretcher {
        val tab = Stretcher(8.0, 8.0)
        tab.fill = Color.WHITE
        tab.x = x
        tab.y = y
        tabs.add(tab)
        canvas.canvasPane.children.add(tab)
        return tab
    }

    private fun close() {
        canvas.canvasPane.children.remove(path)
        for(tab in tabs)
            canvas.canvasPane.children.remove(tab)
    }

    override fun handleMousePress(event: MouseEvent) {
        if(event.target == path) {
            close()
            canvas.selectionControl = SelectionController(canvas)
        }
    }

    override fun handleMouseDrag(event: MouseEvent) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun handleMouseRelease(event: MouseEvent) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun handleDoubleClick(event: MouseEvent) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun handleMouseClick(event: MouseEvent) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}