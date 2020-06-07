package xute.markdeditor.utilities

import xute.markdeditor.styles.TextComponentStyle

object MarkDownFormat {
    fun getTextFormat(heading: Int, content: String?): String {
        val pref: String = when (heading) {
            TextComponentStyle.HEADING_H1 -> "# "
            TextComponentStyle.HEADING_H2 -> "## "
            TextComponentStyle.HEADING_H3 -> "### "
            TextComponentStyle.HEADING_H4 -> "#### "
            TextComponentStyle.HEADING_H5 -> "##### "
            TextComponentStyle.QUOTE_ITALIC -> "> "
            else -> ""
        }
        return String.format("\\n%s%s\\n", pref, content)
    }

    fun getImageFormat(url: String?): String {
        return String.format("\\n<center>![Image](%s)</center>", url)
    }

    fun getCaptionFormat(caption: String?): String {
        return if (caption != null) String.format("<center>%s</center>\\n\\n\\n", caption) else "\\n\\n\\n"
    }

    const val lineFormat: String = "\\n\\n---\\n\\n"

    fun getULFormat(content: String?): String {
        return String.format("  - %s\\n", content)
    }

    fun getOLFormat(indicator: String?, content: String?): String {
        return String.format("  %s %s\\n", indicator, content)
    }
}