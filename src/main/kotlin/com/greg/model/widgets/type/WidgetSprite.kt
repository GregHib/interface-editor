package com.greg.model.widgets.type

import com.greg.controller.utils.MathUtils
import com.greg.model.settings.Settings
import com.greg.model.widgets.WidgetBuilder
import com.greg.model.widgets.properties.extended.IntProperty
import com.greg.model.widgets.properties.extended.ObjProperty
import com.greg.model.widgets.properties.extended.StringProperty

class WidgetSprite(builder: WidgetBuilder, id: Int) : Widget(builder, id) {

    private var cap: ObjProperty<IntRange>? = null
    private var sprite: IntProperty? = null
    private var archive: StringProperty? = null

    init {
        properties.add(widthProperty(), "Layout").property.setDisabled(true)
        properties.add(heightProperty(), "Layout").property.setDisabled(true)
        properties.addCapped(spriteProperty(), capProperty())
        properties.add(archiveProperty())
    }

    fun getSprite(): Int {
        return spriteProperty().get()
    }

    fun setSprite(value: Int, contrain: Boolean = true) {
        spriteProperty().set(if(contrain) MathUtils.constrain(value, getCap().start, getCap().endInclusive) else value)
    }

    fun spriteProperty(): IntProperty {
        if (sprite == null)
            sprite = IntProperty(this, "sprite", Settings.getInt(Settings.DEFAULT_SPRITE_ID))

        return sprite!!
    }

    fun setCap(range: IntRange) {
        capProperty().set(range)
    }

    fun getCap(): IntRange {
        return capProperty().get()
    }

    fun capProperty(): ObjProperty<IntRange> {
        if (cap == null)
            cap = ObjProperty(this, "cap", IntRange(0, 1))

        return cap!!
    }

    fun getArchive(): String {
        return archiveProperty().get()
    }

    fun setArchive(value: String) {
        archiveProperty().set(value)
    }

    fun archiveProperty(): StringProperty {
        if (archive == null)
            archive = StringProperty(this, "archive", Settings.get(Settings.DEFAULT_SPRITE_ARCHIVE_NAME))

        return archive!!
    }
}