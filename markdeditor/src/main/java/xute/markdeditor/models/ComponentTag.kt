package xute.markdeditor.models

class ComponentTag {
    var componentIndex = 0
    var component: BaseComponentModel? = null

    override fun toString(): String {
        return "ComponentTag{" +
                "componentIndex=" + componentIndex +
                ", baseComponent=" + component +
                '}'
    }
}