package xute.markdeditor.utilities

import android.view.View
import xute.markdeditor.MarkDEditor
import xute.markdeditor.components.HorizontalDividerComponentItem
import xute.markdeditor.components.ImageComponentItem
import xute.markdeditor.components.TextComponentItem
import xute.markdeditor.datatype.DraftDataItemModel
import xute.markdeditor.models.ComponentTag
import xute.markdeditor.models.DraftModel
import xute.markdeditor.models.TextComponentModel
import xute.markdeditor.styles.TextFormatType
import xute.markdeditor.styles.TextHeadingStyle
import xute.markdeditor.styles.TextModeType
import java.util.*

class DraftManager {
    /**
     * Traverse through each item and prepares the draft item list.
     *
     * @param markDEditor editor object.
     * @return a list of Draft Item types.
     */
    fun processDraftContent(markDEditor: MarkDEditor): DraftModel {
        val drafts = ArrayList<DraftDataItemModel>()
        val childCount = markDEditor.childCount
        var view: View
        @TextFormatType var textFormatType: Int
        @TextFormatType var textHeadingStyle: Int
        var componentTag: ComponentTag
        for (i in 0 until childCount) {
            view = markDEditor.getChildAt(i)
            if (view is TextComponentItem) {
                //check mode
                val mode = view.getMode()
                if (mode == TextModeType.MODE_PLAIN) {
                    //check for styles {H1-H5, BlockQuote and Normal}
                    componentTag = view.getTag() as ComponentTag
                    (componentTag.component as? TextComponentModel)?.let { textComponentModel ->
                        textFormatType = textComponentModel.textFormatType
                        textHeadingStyle = textComponentModel.textHeadingStyle

                        drafts.add(getPlainModel(textFormatType, textHeadingStyle, view.getContent()))
                    }
                } else if (mode == TextModeType.MODE_UL) {
                    drafts.add(getUlModel(view.getContent()))
                } else if (mode == TextModeType.MODE_OL) {
                    drafts.add(getOlModel(view.getContent()))
                }
            } else if (view is HorizontalDividerComponentItem) {
                drafts.add(hRModel)
            } else if (view is ImageComponentItem) {
                drafts.add(getImageModel(
                        view.getDownloadUrl(),
                        view.mCaption
                ))
            }
        }
        return DraftModel(drafts)
    }

    /**
     * Models Text information.
     *
     * @param textType  style associated with the text (NORMAL,H1-H5,BLOCKQUOTE)
     * @param headingStyle style associated with the text (NORMAL,H1-H5,BLOCKQUOTE)
     * @param content   text content
     * @return a Generic TextType Object containing information.
     */
    private fun getPlainModel(@TextFormatType textType: Int, @TextFormatType headingStyle: Int, content: String): DraftDataItemModel {
        val dataItemModel = DraftDataItemModel()
        dataItemModel.itemType = DraftModel.ITEM_TYPE_TEXT
        dataItemModel.content = content
        dataItemModel.mode = TextModeType.MODE_PLAIN
        dataItemModel.textType = textType
        dataItemModel.headingStyle = headingStyle
        return dataItemModel
    }

    /**
     * Models UnOrdered list text information.
     *
     * @param content text content.
     * @return a UL type model object.
     */
    private fun getUlModel(content: String): DraftDataItemModel {
        val dataItemModel = DraftDataItemModel()
        dataItemModel.itemType = DraftModel.ITEM_TYPE_TEXT
        dataItemModel.content = content
        dataItemModel.mode = TextModeType.MODE_UL
        dataItemModel.textType = TextFormatType.FORMAT_LIST
        dataItemModel.textType = TextHeadingStyle.HEADING_NORMAL
        return dataItemModel
    }

    /**
     * Models Ordered list text information.
     *
     * @param content text content.
     * @return a OL type model object.
     */
    private fun getOlModel(content: String): DraftDataItemModel {
        val dataItemModel = DraftDataItemModel()
        dataItemModel.itemType = DraftModel.ITEM_TYPE_TEXT
        dataItemModel.content = content
        dataItemModel.mode = TextModeType.MODE_OL
        dataItemModel.textType = TextFormatType.FORMAT_LIST
        dataItemModel.textType = TextHeadingStyle.HEADING_NORMAL
        return dataItemModel
    }

    /**
     * Models Horizontal rule object.
     *
     * @return a HR type model object.
     */
    private val hRModel: DraftDataItemModel
        private get() {
            val hrType = DraftDataItemModel()
            hrType.itemType = DraftModel.ITEM_TYPE_HR
            return hrType
        }

    /**
     * Models Image type object item.
     *
     * @param downloadUrl url of the image.
     * @param caption     caption of the image(if any)
     * @return a Image Model object type.
     */
    private fun getImageModel(downloadUrl: String?, caption: String?): DraftDataItemModel {
        val imageType = DraftDataItemModel()
        imageType.itemType = DraftModel.ITEM_TYPE_IMAGE
        imageType.caption = caption
        imageType.downloadUrl = downloadUrl
        return imageType
    }
}