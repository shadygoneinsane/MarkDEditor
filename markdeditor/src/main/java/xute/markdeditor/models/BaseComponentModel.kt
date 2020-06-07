package xute.markdeditor.models

open class BaseComponentModel {
    private var componentType: String? = null
    private val componentIndex = 0

    override fun toString(): String {
        return "BaseComponentModel{" +
                "componentType='" + componentType + '\'' +
                ", componentIndex=" + componentIndex +
                '}'
    }
}