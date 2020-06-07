package xute.markdeditor.styles

import androidx.annotation.IntDef

@Target(AnnotationTarget.CLASS, AnnotationTarget.PROPERTY, AnnotationTarget.VALUE_PARAMETER, AnnotationTarget.TYPE)
@IntDef(TextComponentStyle.FORMAT_NORMAL,

        TextComponentStyle.HEADING_H1, TextComponentStyle.HEADING_H2, TextComponentStyle.HEADING_H3,
        TextComponentStyle.HEADING_H4, TextComponentStyle.HEADING_H5,

        TextComponentStyle.QUOTE_ITALIC, TextComponentStyle.QUOTE_H2_CENTER
)
annotation class TextComponentStyle {
    companion object {
        /**
         * Format normal
         */
        const val FORMAT_NORMAL: Int = 0

        /**
         * Format header i.e Normal, H1 .. H5
         */
        const val HEADING_H1: Int = 1
        const val HEADING_H2: Int = 2
        const val HEADING_H3: Int = 3
        const val HEADING_H4: Int = 4
        const val HEADING_H5: Int = 5

        /**
         * Format quote i.e normal, italic and spaced H2
         */
        const val QUOTE_ITALIC: Int = 6
        const val QUOTE_H2_CENTER: Int = 7
    }
}

fun @TextComponentStyle Int.nextHeadingStyle(): @TextComponentStyle Int {
    return when (this) {
        TextComponentStyle.FORMAT_NORMAL -> TextComponentStyle.HEADING_H1
        TextComponentStyle.HEADING_H1 -> TextComponentStyle.HEADING_H2
        TextComponentStyle.HEADING_H2 -> TextComponentStyle.HEADING_H3
        TextComponentStyle.HEADING_H3 -> TextComponentStyle.HEADING_H4
        TextComponentStyle.HEADING_H4 -> TextComponentStyle.HEADING_H5
        TextComponentStyle.HEADING_H5 -> TextComponentStyle.FORMAT_NORMAL

        else -> TextComponentStyle.FORMAT_NORMAL
    }
}

fun @TextComponentStyle Int.nextBlockQuoteStyle(): @TextComponentStyle Int {
    return when (this) {
        TextComponentStyle.FORMAT_NORMAL -> TextComponentStyle.QUOTE_ITALIC
        TextComponentStyle.QUOTE_ITALIC -> /*TextComponentStyle.QUOTE_H2_CENTER
        TextComponentStyle.QUOTE_H2_CENTER ->*/ TextComponentStyle.FORMAT_NORMAL

        else -> TextComponentStyle.FORMAT_NORMAL
    }
}

fun @TextComponentStyle Int.returnMaxHeading(): @TextComponentStyle Int {
    return when (this) {
        in TextComponentStyle.HEADING_H1..TextComponentStyle.HEADING_H5 -> {
            this
        }
        else -> {
            TextComponentStyle.HEADING_H5
        }
    }
}