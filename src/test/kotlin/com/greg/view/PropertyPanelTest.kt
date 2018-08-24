package com.greg.view
import com.greg.model.widgets.properties.extended.IntProperty
import com.greg.model.widgets.properties.extended.StringProperty
import javafx.application.Application
import javafx.beans.property.Property
import javafx.beans.value.ObservableValue
import org.controlsfx.control.PropertySheet
import tornadofx.*
import java.util.*

class PropertyPanelTest : View() {

    private val sheet = PropertySheet()

    override val root = drawer {
        item("Properties", expanded = true) {
            vbox {
                add(sheet)
                button("Check") {
                    setOnMouseClicked {
                        println("${property.anInt} ${property.aString}")
                    }
                }
            }
        }

        prefWidth = 300.0
        prefHeight = 300.0
    }

    private val property = PropertyPanelProperty()

    init {
        sheet.items.add(PropertyPanelItem("Numbers", property.anInt))
        sheet.items.add(PropertyPanelItem("Text", property.aString))
    }

    class PropertyPanelProperty {
        var anInt: IntProperty = IntProperty("anInt", 1)
        var aString: StringProperty = StringProperty("aString", "Null")
    }

    private class PropertyPanelItem(private val propertyCategory: String, private var propertyValue: Property<*>) : PropertySheet.Item {
        override fun setValue(value: Any?) {
            propertyValue.value = value
        }

        override fun getName(): String {
            return propertyValue.name.capitalize()
        }

        override fun getDescription(): String? {
            return null
        }

        override fun getType(): Class<*> {
            return propertyValue.value.javaClass
        }

        override fun getValue(): Any {
            return propertyValue.value
        }

        override fun getObservableValue(): Optional<ObservableValue<out Any>> {
            return Optional.empty()
        }

        override fun getCategory(): String {
            return propertyCategory
        }

    }
}

class PropertyPanelTestApp: App(PropertyPanelTest::class)

fun main(args: Array<String>) {
    Application.launch(PropertyPanelTestApp::class.java, *args)
}