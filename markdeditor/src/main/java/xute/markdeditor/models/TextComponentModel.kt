package xute.markdeditor.models

import xute.markdeditor.styles.BlockQuoteStyle
import xute.markdeditor.styles.TextFormatType
import xute.markdeditor.styles.TextHeadingStyle

class TextComponentModel : BaseComponentModel() {
    /**
     * Defines what type of [TextFormatType] component this model is
     */
    @TextFormatType
    var textFormatType: Int = TextFormatType.FORMAT_NORMAL

    /**
     * Defines what type of [TextHeadingStyle] component this model is (if this is format heading)
     */
    @TextHeadingStyle
    var textHeadingStyle: Int = TextHeadingStyle.HEADING_NORMAL

    /**
     * Defines what type of [BlockQuoteStyle] quote this model is (if this is format quote)
     */
    @BlockQuoteStyle
    var blockQuoteStyle: Int = BlockQuoteStyle.QUOTE_NORMAL

    override fun toString(): String {
        return "TextComponentModel{" +
                "headingStyle=" + textFormatType +
                '}'
    }
}