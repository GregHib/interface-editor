package com.greg.model.cache.archives

import com.greg.controller.widgets.WidgetsController
import com.greg.model.cache.archives.widget.WidgetDataConverter
import rs.dusk.cache.Cache
import rs.dusk.cache.definition.data.InterfaceDefinition
import rs.dusk.cache.definition.decoder.InterfaceDecoder

class ArchiveInterface : CacheArchive() {

    companion object {

        internal lateinit var decoder: InterfaceDecoder

        fun lookup(id: Int): InterfaceDefinition? {
            return decoder.getOrNull(id)
        }
    }

    fun save(widgets: WidgetsController, cache: Cache): Boolean {
        return false
    }

    override fun reset(): Boolean {
        return true
    }

    override fun load(cache: Cache): Boolean {
        decoder = InterfaceDecoder(cache)
        println("Found ${decoder.size} interfaces.")
        return true
    }

    fun display(widgets: WidgetsController, index: Int) {
        val data = lookup(index) ?: return
        val widget = WidgetDataConverter.create(data)
        widgets.add(widget)
    }
}