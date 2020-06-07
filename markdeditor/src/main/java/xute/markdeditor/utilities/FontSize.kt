package xute.markdeditor.utilities

import xute.markdeditor.styles.TextComponentStyle

object FontSize {
    private const val H1_SIZE = 32
    private const val H2_SIZE = 28
    private const val H3_SIZE = 24
    private const val H4_SIZE = 22
    private const val H5_SIZE = 20
    private const val NORMAL = 20

    fun getFontSize(@TextComponentStyle heading: Int): Int {
        return when (heading) {
            TextComponentStyle.HEADING_H1 -> H1_SIZE
            TextComponentStyle.HEADING_H2 -> H2_SIZE
            TextComponentStyle.HEADING_H3 -> H3_SIZE
            TextComponentStyle.HEADING_H4 -> H4_SIZE
            TextComponentStyle.HEADING_H5 -> H5_SIZE
            TextComponentStyle.QUOTE_H3_LIGHT -> H3_SIZE
            else -> NORMAL
        }
    }
}