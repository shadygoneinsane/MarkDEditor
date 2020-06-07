package xute.markdeditor.utilities

import xute.markdeditor.MarkDEditor
import xute.markdeditor.datatype.DraftDataItemModel
import xute.markdeditor.models.DraftModel
import xute.markdeditor.styles.TextModeType
import java.util.*

class RenderingUtils {
    private var markDEditor: MarkDEditor? = null
    fun setEditor(markDEditor: MarkDEditor?) {
        this.markDEditor = markDEditor
    }

    fun render(contents: ArrayList<DraftDataItemModel>) {
        //visit each item type
        for (i in contents.indices) {
            val item = contents[i]
            //identify item of data
            if (item.itemType == DraftModel.ITEM_TYPE_TEXT) {
                //identify mode of text item
                when (item.mode) {
                    //includes NORMAL, H1-H5, Blockquote
                    TextModeType.MODE_PLAIN -> renderPlainData(item)

                    //renders orderedList
                    TextModeType.MODE_OL -> renderOrderedList(item)

                    //renders unorderedList
                    TextModeType.MODE_UL -> renderUnOrderedList(item)

                    //default goes to normal text
                    else -> renderPlainData(item)
                }
            } else if (item.itemType == DraftModel.ITEM_TYPE_HR) {
                renderHR()
            } else if (item.itemType == DraftModel.ITEM_TYPE_IMAGE) {
                renderImage(item)
            }
        }
    }

    /**
     * Sets mode to plain and insert a a text component.
     *
     * @param item model of text data item
     */
    private fun renderPlainData(item: DraftDataItemModel) {
        markDEditor?.setCurrentInputMode(TextModeType.MODE_PLAIN)
        markDEditor?.setDataInView(TextModeType.MODE_PLAIN, item.textType)
    }

    /**
     * Sets mode to ordered-list and insert a a text component.
     *
     * @param item model of text data item.
     */
    private fun renderOrderedList(item: DraftDataItemModel) {
        markDEditor?.setCurrentInputMode(TextModeType.MODE_OL)
        markDEditor?.addTextComponent(insertIndex(), item.content)
    }

    /**
     * Sets mode to unordered-list and insert a a text component.
     *
     * @param item model of text data item.
     */
    private fun renderUnOrderedList(item: DraftDataItemModel) {
        markDEditor?.setCurrentInputMode(TextModeType.MODE_UL)
        markDEditor?.addTextComponent(insertIndex(), item.content)
    }

    /**
     * Adds Horizontal line.
     */
    private fun renderHR() {
        markDEditor?.insertHorizontalDivider(false)
    }

    /**
     * @param item model of image item.
     * Inserts image.
     * Sets caption
     */
    private fun renderImage(item: DraftDataItemModel) {
        markDEditor?.insertImage(insertIndex(), item.downloadUrl, true, item.caption)
    }

    /**
     * Since childs are going to be arranged in linear fashion, child count can act as insert index.
     *
     * @return insert index.
     */
    private fun insertIndex(): Int = markDEditor?.childCount!!

}