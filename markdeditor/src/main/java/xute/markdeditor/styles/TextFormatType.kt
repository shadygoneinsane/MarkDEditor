package xute.markdeditor.styles

import androidx.annotation.IntDef

@IntDef(TextFormatType.FORMAT_NORMAL, TextFormatType.FORMAT_HEADER,
        TextFormatType.FORMAT_QUOTE, TextFormatType.FORMAT_LIST,
        TextFormatType.FORMAT_LINK, TextFormatType.ADD_SECTION,
        TextFormatType.MENTION_USER, TextFormatType.ADD_MEDIA)
annotation class TextFormatType {
    companion object {
        /**
         * Format normal
         */
        const val FORMAT_NORMAL: Int = 0

        /**
         * Format header i.e Normal, H1 .. H5
         */
        const val FORMAT_HEADER: Int = 1

        /**
         * Format quote i.e normal, italic and spaced H2
         */
        const val FORMAT_QUOTE: Int = 3

        /**
         * Format list i.2 bullet list, numbered list or normal
         */
        const val FORMAT_LIST: Int = 4

        /**
         * format hyper-link type
         */
        const val FORMAT_LINK: Int = 5

        /**
         * format section
         */
        const val ADD_SECTION: Int = 6

        /**
         * format @user type i.e annotated user profile
         */
        const val MENTION_USER: Int = 7

        /**
         * format media type
         */
        const val ADD_MEDIA: Int = 8
    }
}