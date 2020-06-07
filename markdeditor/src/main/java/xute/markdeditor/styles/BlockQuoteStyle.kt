package xute.markdeditor.styles

import androidx.annotation.IntDef

/**
 * BlockQuote style based on Medium Text Editor
 * Created by: Vikesh Dass
 * Created on: 06-06-2020
 */
@Target(AnnotationTarget.CLASS, AnnotationTarget.PROPERTY, AnnotationTarget.VALUE_PARAMETER, AnnotationTarget.TYPE)
@IntDef(BlockQuoteStyle.QUOTE_NORMAL, BlockQuoteStyle.QUOTE_ITALIC,
        BlockQuoteStyle.QUOTE_CENTER_H2)
annotation class BlockQuoteStyle {
    companion object {
        const val QUOTE_NORMAL = 0
        const val QUOTE_ITALIC = 1
        const val QUOTE_CENTER_H2 = 2
    }
}

fun @BlockQuoteStyle Int.nextBlockQuoteStyle(): @BlockQuoteStyle Int {
    return when (this) {
        BlockQuoteStyle.QUOTE_NORMAL -> BlockQuoteStyle.QUOTE_ITALIC

        BlockQuoteStyle.QUOTE_ITALIC -> BlockQuoteStyle.QUOTE_CENTER_H2

        else -> BlockQuoteStyle.QUOTE_NORMAL
    }
}