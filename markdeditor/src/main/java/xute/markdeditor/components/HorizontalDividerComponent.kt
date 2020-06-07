package xute.markdeditor.components

import android.content.Context

class HorizontalDividerComponent(var context: Context) {
    fun newHorizontalComponentItem(): HorizontalDividerComponentItem = HorizontalDividerComponentItem(context)
}