package com.greg.properties

import com.greg.canvas.widget.Widget
import com.greg.canvas.widget.WidgetText
import com.greg.controller.Controller
import javafx.scene.control.Label
import javafx.scene.control.TextField
import kotlin.jvm.internal.Reflection
import kotlin.reflect.KClass
import kotlin.reflect.full.allSuperclasses

class PropertyPanel(private var controller: Controller) {

    fun refresh() {
        println("Refresh properties panel")

        controller.propertyPanel.children.clear()

        if(controller.canvas.selectionGroup.getGroup().size == 0) {

        } else if(controller.canvas.selectionGroup.getGroup().size == 1) {
            loadProperties(controller.canvas.selectionGroup.getGroup().first())
        } else {
            for (i in controller.canvas.selectionGroup.getGroup()) {
                println(i)
            }
        }
    }

    private fun loadProperties(widget: Widget) {

        var kotlinClass = Reflection.getOrCreateKotlinClass(widget.javaClass)

        handleStuff(kotlinClass)

        for(child in kotlinClass.allSuperclasses) {

            handleStuff(child)

            if(child is Widget)
                break
        }
    }

    private fun handleStuff(child: KClass<*>) {
        if(child == WidgetText::class) {
            var segment = AttributeSegment(child.simpleName)

            val attribute = Attribute()
            var label = Label("Text")
            val spacer = AttributeSpacer()
            var text = TextField()
            attribute.children.addAll(label, spacer, text)

            segment.add(attribute)

            controller.propertyPanel.children.add(segment)
        }
    }
}