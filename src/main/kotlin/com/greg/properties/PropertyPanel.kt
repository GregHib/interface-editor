package com.greg.properties

import com.greg.canvas.widget.Widget
import com.greg.canvas.widget.WidgetText
import com.greg.controller.Controller
import kotlin.jvm.internal.Reflection
import kotlin.reflect.KClass
import kotlin.reflect.full.allSuperclasses

class PropertyPanel(private var controller: Controller) {

    fun refresh() {

        var group = controller.canvas.selectionGroup.getGroup()
        controller.propertyPanel.children.clear()

        when {
            group.size == 0 -> controller.propertyPanel.children.add(AttributeSegment("No Selection"))
            group.size == 1 -> loadProperties(group.first())
            else -> for (i in group) {
                println(i)
            }
        }
    }

    private fun loadProperties(widget: Widget) {

        var kotlinClass = Reflection.getOrCreateKotlinClass(widget.javaClass)

        handleStuff(widget, kotlinClass)

        for(child in kotlinClass.allSuperclasses) {

            handleStuff(widget, child)

            if(child is Widget)
                break
        }
    }

    private fun handleStuff(widget: Widget, child: KClass<*>) {
        if(child == WidgetText::class) {
            var widgetText = widget as WidgetText
            var segment = AttributeSegment(child.simpleName)

            var attribute = widgetText.getAttributes()

            segment.add(attribute)

            controller.propertyPanel.children.add(segment)
        }
    }
}