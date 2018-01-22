package src.com.greg.model.widgets

open class WidgetBuilder(val type: WidgetType = WidgetType.WIDGET) {

    companion object {
        var identifier = 0

        fun getId() : Int {
            return identifier++
        }
    }

    open fun build(id: Int = -1): Widget {
        val identifier = if(id != -1) id else getId()
        return when(type) {
            WidgetType.TEXT -> WidgetText(this, identifier)
            WidgetType.RECTANGLE -> WidgetRectangle(this, identifier)
            else -> {
                Widget(this, identifier)
            }
        }
    }
}