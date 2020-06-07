package xute.markdeditor.styles

import androidx.annotation.IntDef

@Target(AnnotationTarget.CLASS, AnnotationTarget.PROPERTY, AnnotationTarget.VALUE_PARAMETER, AnnotationTarget.TYPE)
@IntDef(TextHeadingStyle.HEADING_NORMAL, TextHeadingStyle.HEADING_H1,
        TextHeadingStyle.HEADING_H2, TextHeadingStyle.HEADING_H3,
        TextHeadingStyle.HEADING_H4, TextHeadingStyle.HEADING_H5)
annotation class TextHeadingStyle {
    companion object {
        const val HEADING_NORMAL: Int = 0
        const val HEADING_H1: Int = 1
        const val HEADING_H2: Int = 2
        const val HEADING_H3: Int = 3
        const val HEADING_H4: Int = 4
        const val HEADING_H5: Int = 5
    }
}

fun @TextHeadingStyle Int.nextHeadingStyle(): @TextHeadingStyle Int {
    return when (this) {
        TextHeadingStyle.HEADING_NORMAL -> TextHeadingStyle.HEADING_H1
        TextHeadingStyle.HEADING_H1 -> TextHeadingStyle.HEADING_H2
        TextHeadingStyle.HEADING_H2 -> TextHeadingStyle.HEADING_H3
        TextHeadingStyle.HEADING_H3 -> TextHeadingStyle.HEADING_H4
        TextHeadingStyle.HEADING_H4 -> TextHeadingStyle.HEADING_H5
        TextHeadingStyle.HEADING_H5 -> TextHeadingStyle.HEADING_NORMAL

        else -> TextHeadingStyle.HEADING_NORMAL
    }
}

fun @TextHeadingStyle Int.returnMaxHeading(): @TextHeadingStyle Int {
    return when (this) {
        in TextHeadingStyle.HEADING_H1..TextHeadingStyle.HEADING_H5 -> {
            this
        }
        else -> {
            TextHeadingStyle.HEADING_H5
        }
    }
}