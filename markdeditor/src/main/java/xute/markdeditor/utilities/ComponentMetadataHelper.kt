package xute.markdeditor.utilities

import xute.markdeditor.models.ComponentTag

object ComponentMetadataHelper {
    fun getNewComponentTag(index: Int): ComponentTag {
        val componentTag = ComponentTag()
        componentTag.componentIndex = index
        return componentTag
    }
}