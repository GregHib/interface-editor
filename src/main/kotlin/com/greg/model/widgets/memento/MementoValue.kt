package src.com.greg.model.widgets.memento

import javafx.beans.property.IntegerProperty
import javafx.beans.property.ObjectProperty
import javafx.beans.property.Property
import javafx.scene.paint.Color

data class MementoValue(val value: String) {
    fun convert(property: Property<*>): Any? {
        return when (property) {
            is IntegerProperty -> value.toInt()
            is ObjectProperty -> {
                if(value.startsWith("0x"))
                    return Color.valueOf(value)
                else
                    value
            }
            else -> value
        }
    }

    override fun toString(): String {
        return value
    }
}