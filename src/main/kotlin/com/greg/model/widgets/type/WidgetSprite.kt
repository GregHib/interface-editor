package com.greg.model.widgets.type

import com.greg.model.settings.Settings
import com.greg.model.widgets.WidgetBuilder
import com.greg.model.widgets.properties.extended.IntProperty
import com.greg.model.widgets.properties.extended.ObjProperty
import com.greg.model.widgets.properties.extended.StringProperty
import com.greg.view.sprites.SpriteController

class WidgetSprite(builder: WidgetBuilder, id: Int) : Widget(builder, id) {

    private var cap: ObjProperty<IntRange>? = null
    private var sprite: IntProperty? = null
    private var archive: StringProperty? = null

    fun getSprite(): Int {
        return spriteProperty().get()
    }

    fun setSprite(value: Int) {
        spriteProperty().set(value)
    }

    fun spriteProperty(): IntProperty {
        if (sprite == null)
            sprite = IntProperty(this, "sprite", Settings.getInt(Settings.DEFAULT_SPRITE_ID))

        return sprite!!
    }


    fun setCap(range: IntRange) {
        capProperty().set(range)
    }

    fun capProperty(): ObjProperty<IntRange> {
        if (cap == null)
            cap = ObjProperty(this, "cap", IntRange(0, SpriteController.imageArchiveList[0].sprites.size))

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

    init {
        properties.addCapped(spriteProperty(), capProperty())
        properties.add(archiveProperty())
        widthToggle.property.setDisabled(true)
        heightToggle.property.setDisabled(true)
    }
}