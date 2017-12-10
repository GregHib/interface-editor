package com.greg.canvas.widget

import com.greg.panels.attributes.parts.pane.AttributePaneType
import com.greg.panels.attributes.Attribute
import com.greg.panels.attributes.AttributeType
import javafx.geometry.VPos
import javafx.scene.paint.Color
import javafx.scene.paint.Paint
import javafx.scene.text.Text

class WidgetText : Text, WidgetInterface {

    var properties = mutableListOf<Attribute>()

    init {
//        properties.add(Attribute("Message", "message", AttributeType.TEXT_FIELD, this::class))
        properties.add(Attribute("Text Colour", "strokeProperty", AttributeType.COLOUR_PICKER, this::class))
    }

    //Width and height arguments will be changed as soon as message is set anyway.
    constructor(string: String?, colour: Color?) : super(string) {
        message = string
        stroke = colour as Paint
        textOrigin = VPos.TOP
//        this.children.add(text)
    }

    private fun refreshSize() {
//        super.rectangle.width = text.layoutBounds.width
//        super.rectangle.height = text.layoutBounds.height
    }

    private var message: String?
        get() {
            return text
        }
        set(value) {
            text = value
            refreshSize()
        }

    override fun getProperties(type: AttributePaneType): List<Attribute>? {
        if(type == AttributePaneType.PROPERTIES)
            return properties
        return null
    }

    /*override fun refreshGroups(groups: MutableList<AttributeGroup>) {
        val group = groups.first()

//        linkGroup(group, this)

        groups.remove(group)
        super.refreshGroups(groups)
    }

    override fun linkGroups(groups: MutableList<AttributeGroup>) {
        val group = groups.first()

        linkGroup(group, this)

        groups.remove(group)
        super.linkGroups(groups)
    }

    override fun handleReflection(property: Attribute): Any? {
        return (property.reflection as KMutableProperty1<WidgetText, *>).get(this)
    }

    override fun getGroups(): List<AttributeGroup> {
        var groups = mutableListOf<AttributeGroup>()

        groups.add(createGroup(name, widgetClass))

        groups.addAll(super.getGroups())

        return groups
    }*/

}
