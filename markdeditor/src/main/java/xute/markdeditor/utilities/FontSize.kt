package xute.markdeditor.utilities

import xute.markdeditor.styles.TextHeadingStyle

object FontSize {
    private const val H1_SIZE = 32
    private const val H2_SIZE = 28
    private const val H3_SIZE = 24
    private const val H4_SIZE = 22
    private const val H5_SIZE = 20
    private const val NORMAL = 20

    fun getFontSize(@TextHeadingStyle heading: Int): Int {
        return when (heading) {
            TextHeadingStyle.HEADING_H1 -> H1_SIZE
            TextHeadingStyle.HEADING_H2 -> H2_SIZE
            TextHeadingStyle.HEADING_H3 -> H3_SIZE
            TextHeadingStyle.HEADING_H4 -> H4_SIZE
            TextHeadingStyle.HEADING_H5 -> H5_SIZE
            TextHeadingStyle.HEADING_NORMAL -> NORMAL
            else -> H5_SIZE
        }
    }
}