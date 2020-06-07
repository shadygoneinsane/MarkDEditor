package xute.markdeditor.components

import android.content.Context
import xute.markdeditor.components.ImageComponentItem.ImageComponentListener

class ImageComponent(private val context: Context) {
    fun getNewImageComponentItem(imageRemoveListener: ImageComponentListener?): ImageComponentItem {
        val imageComponentItem = ImageComponentItem(context)
        imageComponentItem.setImageComponentListener(imageRemoveListener)
        return imageComponentItem
    }
}