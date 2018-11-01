package com.greg.model.widgets.type.groups

import com.greg.controller.utils.MathUtils
import com.greg.model.widgets.properties.IntValues
import com.greg.model.widgets.properties.extended.BoolProperty
import com.greg.model.widgets.properties.extended.IntProperty
import com.greg.model.widgets.properties.extended.ObjProperty
import com.greg.model.widgets.type.Widget

abstract class GroupWidget {
    abstract var x: IntProperty
    abstract var y: IntProperty
    abstract var width: IntProperty
    abstract var height: IntProperty

    abstract var widthBounds: ObjProperty<IntValues>
    abstract var heightBounds: ObjProperty<IntValues>

    abstract var locked: BoolProperty
    abstract var selected: BoolProperty
    abstract var invisible: BoolProperty
    abstract var hidden: BoolProperty
    abstract var hovered: BoolProperty

    internal abstract var parent: ObjProperty<Widget?>
    abstract var optionType: IntProperty
    abstract var contentType: IntProperty
    abstract var alpha: IntProperty
    abstract var hoverId: IntProperty
    abstract var scriptOperators: ObjProperty<IntArray>
    abstract var scriptDefaults: ObjProperty<IntArray>
    abstract var scripts: ObjProperty<Array<IntArray?>>

    fun setLocked(value: Boolean) {
        locked.set(value)
    }

    fun isLocked(): Boolean {
        return locked.get()
    }

    fun isInvisible(): Boolean {
        return invisible.get()
    }

    fun setInvisible(value: Boolean) {
        invisible.set(value)
    }

    fun isHidden(): Boolean {
        return hidden.get()
    }

    fun setHidden(value: Boolean) {
        hidden.set(value)
    }

    fun isHovered(): Boolean {
        return hovered.get()
    }

    fun setHovered(value: Boolean) {
        hovered.set(value)
    }

    fun isSelected(): Boolean {
        return selected.get()
    }

    fun setSelected(value: Boolean, selection: Boolean = true) {
        (this as? Widget)?.updateSelection = selection
        selected.set(if (value && isLocked()) false else value)
    }

    fun getX(): Int {
        return x.get()
    }

    fun setX(value: Int) {
        x.set(value)
    }

    fun getY(): Int {
        return y.get()
    }

    fun setY(value: Int) {
        y.set(value)
    }

    fun getWidth(): Int {
        return width.get()
    }

    fun setWidth(value: Int) {
        width.set(if(getWidthBounds() != IntValues.EMPTY) MathUtils.constrain(value, getWidthBounds().first, getWidthBounds().last) else value)
    }

    fun getHeight(): Int {
        return height.get()
    }

    fun setHeight(value: Int) {
        height.set(if(getWidthBounds() != IntValues.EMPTY) MathUtils.constrain(value, getHeightBounds().first, getHeightBounds().last) else value)
    }

    fun getWidthBounds(): IntValues {
        return widthBounds.get()
    }

    fun getHeightBounds(): IntValues {
        return heightBounds.get()
    }

    fun setParent(value: Widget?) {
        parent.set(value)
    }

    fun getParent(): Widget? {
        return parent.get()
    }

    fun setOptionType(value: Int) { optionType.set(value) }

    fun getOptionType(): Int { return optionType.get() }

    fun setContentType(value: Int) { contentType.set(value) }

    fun getContentType(): Int { return contentType.get() }

    fun setAlpha(value: Int) { alpha.set(value) }

    fun getAlpha(): Int { return alpha.get() }

    fun setHoverId(value: Int) { hoverId.set(value) }

    fun getHoverId(): Int { return hoverId.get() }

    fun setScriptOperators(value: IntArray) { scriptOperators.set(value) }

    fun getScriptOperators(): IntArray { return scriptOperators.get() }

    fun setScriptDefaults(value: IntArray) { scriptDefaults.set(value) }

    fun getScriptDefaults(): IntArray { return scriptDefaults.get() }

    fun setScripts(value: Array<IntArray?>) { scripts.set(value) }

    fun getScripts(): Array<IntArray?> { return scripts.get() }
}