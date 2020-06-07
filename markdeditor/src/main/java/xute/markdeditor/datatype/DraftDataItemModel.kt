package xute.markdeditor.datatype

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class DraftDataItemModel {
    @Expose
    @SerializedName("itemType")
    var itemType = 0

    @Expose
    @SerializedName("mode")
    var mode = 0

    @Expose
    @SerializedName("type")
    var textType = 0

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
                ", type=" + textType +
                ", content='" + content + '\'' +
                ", downloadUrl='" + downloadUrl + '\'' +
                ", caption='" + caption + '\'' +
                '}'
    }
}