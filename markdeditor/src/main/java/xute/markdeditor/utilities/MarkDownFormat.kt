package xute.markdeditor.utilities

import xute.markdeditor.styles.TextFormatType
import xute.markdeditor.styles.TextHeadingStyle

object MarkDownFormat {
    @JvmStatic
    fun getTextFormat(heading: Int, content: String?): String {
        val pref: String = when (heading) {
            TextHeadingStyle.HEADING_H1 -> "# "
            TextHeadingStyle.HEADING_H2 -> "## "
            TextHeadingStyle.HEADING_H3 -> "### "
            TextHeadingStyle.HEADING_H4 -> "#### "
            TextHeadingStyle.HEADING_H5 -> "##### "
            TextFormatType.ADD_SECTION -> "> "
            else -> ""
        }
        return String.format("\\n%s%s\\n", pref, content)
    }

    @JvmStatic
    fun getImageFormat(url: String?): String {
        return String.format("\\n<center>![Image](%s)</center>", url)
    }

    @JvmStatic
    fun getCaptionFormat(caption: String?): String {
        return if (caption != null) String.format("<center>%s</center>\\n\\n\\n", caption) else "\\n\\n\\n"
    }

    @JvmStatic
    val lineFormat: String = "\\n\\n---\\n\\n"

    @JvmStatic
    fun getULFormat(content: String?): String {
        return String.format("  - %s\\n", content)
    }

    @JvmStatic
    fun getOLFormat(indicator: String?, content: String?): String {
        return String.format("  %s %s\\n", indicator, content)
    }
}