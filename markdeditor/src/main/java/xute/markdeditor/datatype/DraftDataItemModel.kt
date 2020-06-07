package xute.markdeditor.datatype

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import xute.markdeditor.styles.TextComponentStyle
import xute.markdeditor.styles.TextModeType

class DraftDataItemModel {
    @Expose
    @SerializedName("itemType")
    var itemType = TextComponentStyle.FORMAT_NORMAL

    @Expose
    @SerializedName("mode")
    var mode = TextModeType.MODE_PLAIN

    @Expose
    @SerializedName("style")
    var style = 0

    @Expose
    @SerializedName("content")
    var content: String? = null

    @Expose
    @SerializedName("downloadUrl")
    var downloadUrl: String? = null

    @Expose
    @SerializedName("caption")
    var caption: String? = null

    override fun toString(): String {
        return "DraftDataItemModel{" +
                "itemType='" + itemType + '\'' +
                ", mode=" + mode +
                ", style=" + style +
                ", content='" + content + '\'' +
                ", downloadUrl='" + downloadUrl + '\'' +
                ", caption='" + caption + '\'' +
                '}'
    }
}