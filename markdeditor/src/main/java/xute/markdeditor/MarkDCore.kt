package xute.markdeditor

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import xute.markdeditor.components.TextComponentItem
import xute.markdeditor.models.BulletGroupModel
import xute.markdeditor.styles.TextModeType
import java.util.*

open class MarkDCore(context: Context?, attrs: AttributeSet?) : LinearLayout(context, attrs) {
    lateinit var bulletGroupModels: ArrayList<BulletGroupModel>

    init {
        init()
    }

    private fun init() {
        this.orientation = VERTICAL
        bulletGroupModels = ArrayList()
    }

    /**
     * Creates bullet groups and invalidate the view.
     */
    protected fun refreshViewOrder() {
        makeBulletGroups()
        invalidateComponentMode(bulletGroupModels)
    }

    /**
     * This method find the group of bullets.
     * There can be 2 type of group.
     * {UL,StartIndex=0,EndIndex=3}
     * and
     * {OL,StartIndex=5,EndIndex=9}
     *
     *
     * These group are useful for maintaining correct order
     * even when view are inserted and deleted in any way.
     */
    private fun makeBulletGroups() {
        bulletGroupModels.clear()
        var startIndex: Int
        var endIndex = -1
        var child: View?
        val childCount = childCount
        var i = 0
        while (i < childCount) {
            child = getChildAt(i)
            //skip non-text component items
            if (child is TextComponentItem) {
                if (child.getMode() == TextModeType.MODE_OL) {
                    startIndex = i
                    //search end of this group
                    for (j in i until childCount) {
                        i = j
                        child = getChildAt(j)
                        endIndex = if (child is TextComponentItem) {
                            if (child.getMode() == TextModeType.MODE_OL) {
                                i
                            } else {
                                break
                            }
                        } else {
                            break
                        }
                    }
                    //prepare model and add
                    val groupModel = BulletGroupModel()
                    groupModel.orderType = TextModeType.MODE_OL
                    groupModel.startIndex = startIndex
                    groupModel.endIndex = endIndex
                    bulletGroupModels.add(groupModel)
                }
            }
            i++
        }
    }

    /**
     * Helper method to update the bullets.
     * If view are inserted/removed, bullets are reassigned to view,
     * so we need to update the view.
     *
     * @param bulletGroupModels list of groups of bullets.
     */
    private fun invalidateComponentMode(bulletGroupModels: ArrayList<BulletGroupModel>) {
        var ot: Int
        var si: Int
        var ei: Int
        var _tempChild: TextComponentItem
        //loop through each group
        for (i in bulletGroupModels.indices) {
            ot = bulletGroupModels[i].orderType
            si = bulletGroupModels[i].startIndex
            ei = bulletGroupModels[i].endIndex
            if (ot == TextModeType.MODE_OL) {
                //set ol mode
                var ci = 1
                for (j in si..ei) {
                    try {
                        _tempChild = getChildAt(j) as TextComponentItem
                        _tempChild.setMode(TextModeType.MODE_OL)
                        _tempChild.setIndicator("$ci.")
                        ci++
                    } catch (e: Exception) {
                        Log.d("EditorCore", "pos $j")
                        e.printStackTrace()
                    }
                }
            }
        }
    }
}