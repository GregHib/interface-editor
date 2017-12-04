package com.greg.canvas.widget

import com.greg.canvas.DragModel
import com.greg.properties.attributes.PropertyGroup
import javafx.scene.Group
import javafx.scene.paint.Paint

abstract class Widget: Group() {

    lateinit var drag: DragModel
    abstract fun setSelection(colour: Paint?)
    abstract fun getGroup(): List<PropertyGroup>
    abstract fun handleGroup(groups: MutableList<PropertyGroup>)
}