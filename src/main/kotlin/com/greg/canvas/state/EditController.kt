package com.greg.canvas.state

import com.greg.Utils
import com.greg.Utils.Companion.constrain
import com.greg.Utils.Companion.setWidgetDrag
import com.greg.canvas.DragModel
import com.greg.canvas.WidgetCanvas
import com.greg.canvas.edit.Stretcher
import com.greg.canvas.widget.Widget
import com.greg.settings.Settings
import com.greg.settings.SettingsKey
import javafx.beans.binding.DoubleBinding
import javafx.scene.Cursor
import javafx.scene.input.MouseEvent
import javafx.scene.paint.Color
import javafx.scene.shape.Path
import javafx.scene.shape.Rectangle
import javafx.scene.shape.Shape


class EditController(var canvas: WidgetCanvas, var widget: Widget) : PaneController {

    private var path: Shape? = null

    private val node = widget.getNode()
    private val rect = widget.getRectangle().getNode() as Rectangle

    private val x = node.layoutXProperty()
    private val y = node.layoutYProperty()
    private val width = rect.widthProperty()
    private val height = rect.heightProperty()


    private var startX = node.layoutX
    private var startY = node.layoutY
    private var startWidth = rect.layoutBounds.width
    private var startHeight = rect.layoutBounds.height

    private val offset = 0

    private val tabWidth = Settings.getDouble(SettingsKey.DEFAULT_WIDGET_MINIMUM_WIDTH)
    private val tabHeight = Settings.getDouble(SettingsKey.DEFAULT_WIDGET_MINIMUM_HEIGHT)

    private val halfWidth = rect.widthProperty().divide(2.0).subtract(tabWidth / 2.0)
    private val halfHeight = rect.heightProperty().divide(2.0).subtract(tabHeight / 2.0)

    private val left = x.add(offset)
    private val right = x.add(width).subtract(tabWidth).subtract(offset)
    private val top = y.add(offset)
    private val bottom = y.add(height).subtract(tabHeight).subtract(offset)
    private val centreX = x.add(halfWidth)
    private val centreY = y.add(halfHeight)

    private val tabs = mutableListOf<Stretcher>()

    private var click: MouseEvent? = null

    init {
        refresh()

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

    private fun addTab(x: DoubleBinding?, y: DoubleBinding?): Stretcher {
        val tab = Stretcher(8.0, 8.0)
        tab.fill = Color.WHITE
        tab.xProperty().bind(x)
        tab.yProperty().bind(y)
        tabs.add(tab)
        canvas.canvasPane.children.add(tab)
        return tab
    }

    private fun close() {
        canvas.canvasPane.children.remove(path)
        for (tab in tabs)
            canvas.canvasPane.children.remove(tab)
        canvas.selectionControl = SelectionController(canvas)
    }

    override fun handleMousePress(event: MouseEvent) {
        when {
            event.target == path -> close()
            event.target is Stretcher -> {
                this.click = event
                startWidth = width.get()
                startHeight = height.get()
                widget.drag = DragModel(widget.layoutX - event.x, widget.layoutY - event.y)
            }
            event.target is Rectangle -> {
                setWidgetDrag(widget, event, canvas)
            }
        }
    }

    override fun handleMouseDrag(event: MouseEvent) {
        if (click != null && widget.drag != null) {
            val target = click?.target
            if (target is Stretcher) {
                val bounds = canvas.canvasPane.localToScene(canvas.canvasPane.layoutBounds)//TODO what's the difference between this and getCanvasX/Y
                when (tabs.indexOf(target)) {
                    1 -> {//North
                        //The actual positioning of the shape relative to the container
                        var y = event.y + widget.drag!!.offsetY!!

                        val startBottom = click?.y!! + widget.drag!!.offsetY!! + startHeight
                        widget.layoutY = constrain(y, startBottom - tabHeight)
                        widget.setHeight(startBottom - widget.layoutY)
                    }
                    3 -> {//West
                        //The actual positioning of the shape relative to the container
                        var x = event.x + widget.drag!!.offsetX!!

                        val startBottom = click?.x!! + widget.drag!!.offsetX!! + startWidth

                        widget.layoutX = constrain(x, startBottom - tabWidth)
                        widget.setWidth(startBottom - widget.layoutX)
                    }
                    4 -> {//East
                        val offset = startWidth + widget.drag?.offsetX!!
                        val top = click?.x!! + widget.drag?.offsetX!! + tabWidth
                        val bottom = constrain(event.x + offset, top, bounds.width)
                        val difference = bottom - offset - click?.x!!

                        widget.setWidth(startWidth + difference)
                    }
                    6 -> {//South
                        val offset = startHeight + widget.drag?.offsetY!!
                        val top = click?.y!! + widget.drag?.offsetY!! + tabHeight
                        val bottom = constrain(event.y + offset, top, bounds.height)
                        val difference = bottom - offset - click?.y!!

                        widget.setHeight(startHeight + difference)
                    }
                }
            }
        } else if (event.target is Rectangle) {//Dragging
            //Move
            Utils.moveInCanvas(event, canvas, widget)
        }
    }

    override fun handleMouseRelease(event: MouseEvent) {
        click = null
        widget.drag = null
    }

    override fun handleDoubleClick(event: MouseEvent) {
    }

    override fun handleMouseClick(event: MouseEvent) {
    }

    override fun refresh() {
        //Better way rather than remove/recreate every time?
        val mask = Rectangle(x.get(), y.get(), width.get(), height.get())
        val rectangle = Rectangle(765.0, 503.0)
        canvas.canvasPane.children.remove(path)
        path = Path.subtract(rectangle, mask)
        path?.fill = Color.rgb(0, 0, 155, 0.6)
        canvas.canvasPane.children.add(path)
    }
}