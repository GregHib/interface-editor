package src.com.greg.view

import javafx.beans.value.ObservableValue
import javafx.scene.paint.Color
import org.controlsfx.control.PropertySheet
import java.time.Month
import java.time.LocalDate
import java.util.*

class CustomPropertyItem : PropertySheet.Item {
    companion object {

        var customDataMap = mutableMapOf<String, Any>()
        init {

            customDataMap["basic.My Text"] = "Same text" // Creates a TextField in property sheet
            customDataMap["basic.My Date"] = LocalDate.of(2016, Month.JANUARY, 1) // Creates a DatePicker
//        customDataMap.put("misc.My Enum", SomeEnum.ALPHA); // Creates a ChoiceBox
            customDataMap["misc.My Boolean"] = false // Creates a CheckBox
            customDataMap["misc.My Number"] = 500 // Creates a NumericField
            customDataMap["misc.My Color"] = Color.ALICEBLUE // Creates a ColorPicker
        }
    }

    private val key: String
    private var category: String
    private var name: String

    constructor(key: String) {
        this.key = key
        val skey = key.split("\\.".toRegex(), 2).toTypedArray()
        println(Arrays.toString(skey))
        println(key)
        category = skey[0]
        name = skey[1]
    }

    override fun getType(): Class<*> {
        return customDataMap[key]!!::class.java
    }

    override fun getCategory(): String {
        return category
    }

    override fun getName(): String {
        return name
    }

    override fun getDescription(): String? {
        // doesn't really fit into the map
        return null
    }

    override fun getValue(): Any {
        return customDataMap[key]!!
    }

    override fun setValue(value: Any) {
        customDataMap.put(key, value)
    }

    override fun getObservableValue(): Optional<ObservableValue<out Any>> {
        return Optional.empty()
    }

    /*override fun getPropertyEditorClass(): Optional<Class<out PropertyEditor<*>>> {
        // for an item of type number, specify the type of editor to use
        return if (Number::class.java.isAssignableFrom(type)) Optional.of(NumberSliderEditor::class.java) else Optional.empty()

        // ... return other editors for other types

    }*/
}