package xute.markdeditor.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import xute.markdeditor.datatype.DraftDataItemModel
import java.util.*

class DraftModel {
    @Expose
    @SerializedName("draftTitle")
    var draftTitle: String? = null

    @Expose
    @SerializedName("draftId")
    var draftId: Long = 0

    @Expose
    @SerializedName("items")
    var items: ArrayList<DraftDataItemModel>? = null

    constructor(items: ArrayList<DraftDataItemModel>?) {
        this.items = items
    }

    override fun toString(): String {
        return "DraftModel{" +
                "items=" + items +
                '}'
    }

    companion object {
        const val ITEM_TYPE_TEXT = 0
        const val ITEM_TYPE_IMAGE = 1
        const val ITEM_TYPE_HR = 2
    }
}