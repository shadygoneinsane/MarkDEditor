package xute.markdeditor.models

class ImageComponentModel : BaseComponentModel() {
    var url: String? = null
    var caption: String? = null

    override fun toString(): String {
        return "ImageComponentModel{" +
                "url='" + url + '\'' +
                ", caption='" + caption + '\'' +
                '}'
    }
}