package xute.markdeditor.models

import xute.markdeditor.styles.TextComponentStyle

class TextComponentModel : BaseComponentModel() {
    /**
     * Defines what type of [TextComponentStyle] component this model is
     */
    @TextComponentStyle
    var textStyle: Int = TextComponentStyle.FORMAT_NORMAL

    override fun toString(): String {
        return "TextComponentModel{" +
                "headingStyle=" + textStyle +
                '}'
    }
}