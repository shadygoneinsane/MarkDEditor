package xute.markdeditor.styles

import androidx.annotation.IntDef

@IntDef(TextModeType.MODE_PLAIN,
        TextModeType.MODE_UL,
        TextModeType.MODE_OL)
annotation class TextModeType {
    companion object {
        const val MODE_PLAIN: Int = 0
        const val MODE_UL: Int = 1
        const val MODE_OL: Int = 2
    }
}