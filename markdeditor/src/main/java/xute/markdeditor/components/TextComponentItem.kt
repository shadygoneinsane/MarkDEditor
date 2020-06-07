package xute.markdeditor.components

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.TextView
import xute.markdeditor.R
import xute.markdeditor.models.ComponentTag
import xute.markdeditor.models.TextComponentModel
import xute.markdeditor.styles.TextModeType

class TextComponentItem : FrameLayout {
    private val indicatorTv: TextView by lazy { findViewById<TextView>(R.id.indicator) }
    val inputBox: EditText by lazy { findViewById<EditText>(R.id.text) }
    private var mEditorMode = 0
    var indicatorText: String? = null
        private set

    constructor(context: Context) : super(context) {
        init(context, TextModeType.MODE_PLAIN)
    }

    private fun init(context: Context, mode: Int) {
        LayoutInflater.from(context).inflate(R.layout.text_component_item, this)
        setMode(mode)
    }

    constructor(context: Context, mode: Int) : super(context) {
        init(context, mode)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init(context, TextModeType.MODE_PLAIN)
    }

    fun setHintText(hint: String?) {
        inputBox.hint = hint
    }

    fun setText(content: String?) {
        inputBox.setText(content)
    }

    fun getMode(): Int = this.mEditorMode
    fun setMode(mode: Int) {
        this.mEditorMode = mode
        when (mode) {
            TextModeType.MODE_PLAIN -> {
                indicatorTv.visibility = View.GONE
                inputBox.setBackgroundResource(R.drawable.text_input_bg)
            }

            TextModeType.MODE_UL -> {
                indicatorTv.text = UL_BULLET
                indicatorTv.visibility = View.VISIBLE
                inputBox.setBackgroundResource(R.drawable.text_input_bg)
            }

            TextModeType.MODE_OL -> {
                indicatorTv.visibility = View.VISIBLE
                inputBox.setBackgroundResource(R.drawable.text_input_bg)
            }
        }
    }

    //check heading
    fun getTextFormatType(): Int {
        val componentTag = tag as ComponentTag
        //check heading
        return (componentTag.component as TextComponentModel).textFormatType
    }

    //check heading
    fun getTextHeadingStyle(): Int {
        val componentTag = tag as ComponentTag
        //check heading
        return (componentTag.component as TextComponentModel).textHeadingStyle
    }

    fun setIndicator(bullet: String?) {
        indicatorTv.text = bullet
        indicatorText = bullet
    }

    fun getContent(): String = inputBox.text.toString()

    companion object {
        const val UL_BULLET = "\u2022"
    }
}