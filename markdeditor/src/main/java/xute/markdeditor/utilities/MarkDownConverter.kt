package xute.markdeditor.utilities

import android.view.View
import xute.markdeditor.MarkDEditor
import xute.markdeditor.components.HorizontalDividerComponentItem
import xute.markdeditor.components.ImageComponentItem
import xute.markdeditor.components.TextComponentView
import xute.markdeditor.models.ComponentTag
import xute.markdeditor.models.TextComponentModel
import xute.markdeditor.styles.TextModeType
import xute.markdeditor.utilities.MarkDownFormat.getCaptionFormat
import xute.markdeditor.utilities.MarkDownFormat.getImageFormat
import xute.markdeditor.utilities.MarkDownFormat.getOLFormat
import xute.markdeditor.utilities.MarkDownFormat.getTextFormat
import xute.markdeditor.utilities.MarkDownFormat.getULFormat
import xute.markdeditor.utilities.MarkDownFormat.lineFormat

class MarkDownConverter {
    private val stringBuilder: StringBuilder = StringBuilder()
    private val images: ArrayList<String?> = ArrayList()

    /**
     * @return flag whether views are processed or not.
     */
    var isDataProcessed: Boolean = false

    fun processData(markDEditor: MarkDEditor): MarkDownConverter {
        val childCount = markDEditor.childCount
        var view: View
        var textStyle: Int
        var componentTag: ComponentTag
        for (i in 0 until childCount) {
            view = markDEditor.getChildAt(i)
            if (view is TextComponentView) {
                //check mode
                val mode = view.getMode()
                if (mode == TextModeType.MODE_PLAIN) {
                    //check for styles {Normal, heading = H1-H5 or for Quote}
                    componentTag = view.getTag() as ComponentTag
                    (componentTag.component as? TextComponentModel)?.let { textComponentModel ->
                        textStyle = textComponentModel.textStyle
                        stringBuilder.append(getTextFormat(textStyle, view.getContent()))
                    }
                } else if (mode == TextModeType.MODE_UL) {
                    stringBuilder.append(getULFormat(view.getContent()))
                } else if (mode == TextModeType.MODE_OL) {
                    stringBuilder.append(getOLFormat(
                            view.indicatorText,
                            view.getContent()
                    ))
                }
            } else if (view is HorizontalDividerComponentItem) {
                stringBuilder.append(lineFormat)
            } else if (view is ImageComponentItem) {
                stringBuilder.append(getImageFormat(view.getDownloadUrl()))
                images.add(view.getDownloadUrl())
                stringBuilder.append(getCaptionFormat(view.mCaption))
            }
        }
        isDataProcessed = true
        return this
    }

    /**
     * @return markdown format of data.
     */
    val markDown: String = stringBuilder.toString()

    /**
     * @return list of inserted images.
     */
    fun getImages(): List<String?> {
        return images
    }
}