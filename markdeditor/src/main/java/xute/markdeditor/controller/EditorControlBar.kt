package xute.markdeditor.controller

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import xute.markdeditor.MarkDEditor
import xute.markdeditor.MarkDEditor.EditorFocusReporter
import xute.markdeditor.R
import xute.markdeditor.databinding.EditorControlBarBinding
import xute.markdeditor.styles.*

class EditorControlBar : FrameLayout, EditorFocusReporter {
    private var mContext: Context? = null
    private var mEditor: MarkDEditor? = null

    private var enabledColor = 0
    private var disabledColor = 0
    private var customBackgroundColor = 0

    @TextComponentStyle
    private var currentStyle = TextComponentStyle.FORMAT_NORMAL

    /**
     * For defining how many headings the user wants
     * the format is same as ModelComponentStyle
     * default is H5
     */
    private var maxHeading = TextComponentStyle.HEADING_H5

    private var olEnabled = false
    private var ulEnabled = false

    private var editorControlListener: EditorControlListener? = null

    lateinit var viewBinding: EditorControlBarBinding

    constructor(context: Context) : super(context) {
        init(context)
    }

    private fun init(context: Context) {
        mContext = context

        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        viewBinding = DataBindingUtil.inflate(inflater, R.layout.editor_control_bar, this, true)

        setBackgroundAttrs()

        attachListeners()
    }

    private fun setBackgroundAttrs() {
        viewBinding.normalTextBtn.setTextColor(enabledColor)

        viewBinding.headingBtn.setImageResource(R.drawable.ic_h0)
        viewBinding.headingBtn.setColorFilter(R.color.disabled)

        viewBinding.blockQuoteBtn.setImageResource(R.drawable.ic_quote_0)
        viewBinding.blockQuoteBtn.setColorFilter(R.color.disabled)

        viewBinding.headingTextBtn.setTextColor(disabledColor)
        viewBinding.headingNumberBtn.setTextColor(disabledColor)
        viewBinding.bulletBtn.setColorFilter(disabledColor)
        viewBinding.insertLinkBtn.setColorFilter(disabledColor)
        viewBinding.insertHrBtn.setColorFilter(disabledColor)
        viewBinding.insertImageBtn.setColorFilter(disabledColor)

        /*if (maxHeading < TextComponentStyle.H3) {
            viewBinding.container3.visibility = View.GONE
            viewBinding.container2.visibility = View.VISIBLE
        } else {
            viewBinding.container3.visibility = View.VISIBLE
            viewBinding.container2.visibility = View.GONE
        }*/
    }

    private fun init(context: Context, attrs: AttributeSet?) {
        attrs?.let {
            setStyle(context, attrs)
        } ?: setDefaultStyle(context)

        init(context)
    }

    private fun setStyle(context: Context, attrs: AttributeSet) {
        val typedArray = context.theme
                .obtainStyledAttributes(attrs, R.styleable.EditorControlBar, 0, 0)
        try {
            enabledColor = typedArray.getColor(R.styleable.EditorControlBar_enabledColor, ContextCompat.getColor(context, R.color.enabled))
            disabledColor = typedArray.getColor(R.styleable.EditorControlBar_disabledColor, ContextCompat.getColor(context, R.color.disabled))
            customBackgroundColor = typedArray.getColor(R.styleable.EditorControlBar_backgroundColor, ContextCompat.getColor(context, R.color.white))
            customBackgroundColor = typedArray.getColor(R.styleable.EditorControlBar_backgroundColor, ContextCompat.getColor(context, R.color.white))
            maxHeading = (typedArray.getInt(R.styleable.EditorControlBar_maxHeading, 0)).returnMaxHeading()
        } finally {
            typedArray.recycle()
        }
    }

    private fun setDefaultStyle(context: Context) {
        enabledColor = ContextCompat.getColor(context, R.color.enabled)
        disabledColor = ContextCompat.getColor(context, R.color.disabled)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init(context, attrs)
    }

    fun setEditor(editor: MarkDEditor) {
        mEditor = editor
        subscribeForStyles()
    }

    private fun subscribeForStyles() {
        if (mEditor != null) {
            mEditor?.setEditorFocusReporter(this)
        }
    }

    private fun attachListeners() {
        viewBinding.normalTextBtn.setOnClickListener {
            mEditor?.setHeading(componentStyle = TextComponentStyle.FORMAT_NORMAL)
            invalidateControlBarStates(modeComponentStyle = TextModeType.MODE_PLAIN, style = TextComponentStyle.FORMAT_NORMAL)
        }

        viewBinding.headingBtn.setOnClickListener {
            onHeadingClick()
        }

        viewBinding.headingTextBtn.setOnClickListener {
            onHeadingClick()
        }

        //TODO: Would need revisiting !!
        viewBinding.blockQuoteBtn.setOnClickListener {
            currentStyle = currentStyle.nextBlockQuoteStyle()

            when (currentStyle) {
                TextComponentStyle.FORMAT_NORMAL -> {
                    //switch to normal
                    mEditor?.setHeading(componentStyle = TextComponentStyle.FORMAT_NORMAL)

                    invalidateControlBarStates(modeComponentStyle = TextModeType.MODE_PLAIN,
                            style = TextComponentStyle.FORMAT_NORMAL)
                }
                TextComponentStyle.QUOTE_ITALIC -> {
                    //blockQuote
                    mEditor?.changeToBlockQuote(TextComponentStyle.QUOTE_ITALIC)
                    invalidateControlBarStates(modeComponentStyle = TextModeType.MODE_PLAIN,
                            style = TextComponentStyle.FORMAT_NORMAL)
                }

                /*TextComponentStyle.QUOTE_CENTER_H2 -> {
                    //blockQuote with H2 style
                    mEditor?.setHeading(formatType = TextComponentStyle.FORMAT_HEADER,
                            headingStyle = TextComponentStyle.HEADING_H2)
                    invalidateControlBarStates(modeComponentStyle = TextModeType.MODE_PLAIN,
                            formatType = TextComponentStyle.FORMAT_QUOTE,
                            style = TextComponentStyle.HEADING_H2)
                }*/
            }
        }

        viewBinding.bulletBtn.setOnClickListener {
            if (olEnabled) {
                //switch to normal
                mEditor?.setHeading(TextComponentStyle.FORMAT_NORMAL)
                invalidateControlBarStates(TextModeType.MODE_PLAIN, TextComponentStyle.FORMAT_NORMAL)
                olEnabled = false
                ulEnabled = false
            } else if (ulEnabled) {
                // switch to ol mode
                mEditor?.changeToOLMode()
                invalidateControlBarStates(TextModeType.MODE_OL, TextComponentStyle.FORMAT_NORMAL)
                olEnabled = true
                ulEnabled = false
            } else if (!olEnabled && !ulEnabled) {
                // switch to ul mode
                mEditor?.changeToULMode()
                invalidateControlBarStates(TextModeType.MODE_UL, TextComponentStyle.FORMAT_NORMAL)
                ulEnabled = true
                olEnabled = false
            }
        }

        viewBinding.insertHrBtn.setOnClickListener { mEditor?.insertHorizontalDivider() }

        viewBinding.insertLinkBtn.setOnClickListener { editorControlListener?.onInsertLinkClicked() }

        viewBinding.insertImageBtn.setOnClickListener { editorControlListener?.onInsertImageClicked() }
    }

    private fun onHeadingClick() {
        currentStyle = currentStyle.nextHeadingStyle()

        val formatType: Int
        if (currentStyle == TextComponentStyle.FORMAT_NORMAL) {
            mEditor?.setHeading(componentStyle = TextComponentStyle.FORMAT_NORMAL)
        } else {
            mEditor?.setHeading(componentStyle = currentStyle)
        }
        invalidateControlBarStates(modeComponentStyle = TextModeType.MODE_PLAIN, style = currentStyle)
    }

    private fun enableNormalText(enabled: Boolean) {
        if (enabled) {
            viewBinding.normalTextBtn.setTextColor(enabledColor)
        } else {
            viewBinding.normalTextBtn.setTextColor(disabledColor)
        }
    }

    private fun setTextFormatType(@TextComponentStyle style: Int) {
        when (style) {
            TextComponentStyle.FORMAT_NORMAL -> {
                enableBullet(enable = false, isOrdered = false)
                enableBlockQuote(TextComponentStyle.FORMAT_NORMAL)
                currentStyle = TextComponentStyle.FORMAT_NORMAL
                viewBinding.headingTextBtn.setTextColor(disabledColor)
                viewBinding.headingNumberBtn.setTextColor(disabledColor)
                viewBinding.headingNumberBtn.text = "$style"
                viewBinding.headingBtn.setImageResource(R.drawable.ic_h0)
            }

            TextComponentStyle.QUOTE_ITALIC -> {
                enableBlockQuote(style)
                enableNormalText(false)
                enableBullet(enable = false, isOrdered = false)
            }

            in TextComponentStyle.HEADING_H1..TextComponentStyle.HEADING_H5 -> {
                setTextHeadingStyleNew(style)
                setTextHeadingStyle(textHeadingStyle = style)
                enableBullet(enable = false, isOrdered = false)
                enableBlockQuote(TextComponentStyle.FORMAT_NORMAL)
            }
        }
    }

    private fun setTextHeadingStyle(@TextComponentStyle textHeadingStyle: Int) {
        enableNormalText(false)
        viewBinding.headingTextBtn.setTextColor(enabledColor)
        viewBinding.headingNumberBtn.setTextColor(enabledColor)
        viewBinding.headingNumberBtn.text = "$textHeadingStyle"
    }

    private fun setTextHeadingStyleNew(@TextComponentStyle style: Int) {
        when (style) {
            TextComponentStyle.HEADING_H1 -> {
                viewBinding.headingBtn.setImageResource(R.drawable.ic_h1)
            }

            TextComponentStyle.HEADING_H2 -> {
                viewBinding.headingBtn.setImageResource(R.drawable.ic_h2)
            }

            else -> {
                viewBinding.headingBtn.setImageResource(R.drawable.ic_h0)
            }
        }
    }

    private fun enableBullet(enable: Boolean, isOrdered: Boolean) {
        if (enable) {
            if (isOrdered) {
                olEnabled = true
                ulEnabled = false
                viewBinding.bulletBtn.setImageResource(R.drawable.ol)
            } else {
                ulEnabled = true
                olEnabled = false
                viewBinding.bulletBtn.setImageResource(R.drawable.ul)
            }
            viewBinding.bulletBtn.setColorFilter(enabledColor)
        } else {
            ulEnabled = false
            olEnabled = false
            viewBinding.bulletBtn.setImageResource(R.drawable.ul)
            viewBinding.bulletBtn.setColorFilter(disabledColor)
        }
    }

    private fun enableBlockQuote(@TextComponentStyle style: Int) {
        when (style) {
            TextComponentStyle.FORMAT_NORMAL -> {
                //switch to normal
                viewBinding.blockQuoteBtn.setImageResource(R.drawable.ic_quote_0)
            }
            TextComponentStyle.QUOTE_ITALIC -> {
                //blockQuote
                viewBinding.blockQuoteBtn.setImageResource(R.drawable.ic_quote_1)
            }

            TextComponentStyle.QUOTE_H2_CENTER -> {
                //blockQuote with H2 style
                viewBinding.blockQuoteBtn.setImageResource(R.drawable.ic_quote_2)
            }
        }
    }

    private fun invalidateControlBarStates(@TextModeType modeComponentStyle: Int,
                                           @TextComponentStyle style: Int) {
        when (modeComponentStyle) {
            in TextModeType.MODE_OL downTo TextModeType.MODE_UL -> {
                enableBlockQuote(TextComponentStyle.FORMAT_NORMAL)
                enableNormalText(false)
                enableBullet(enable = true, isOrdered = (modeComponentStyle == TextModeType.MODE_OL))
            }

            TextModeType.MODE_PLAIN -> {
                setTextFormatType(style)
            }
        }
    }

    override fun onFocusedViewHas(mode: Int, textComponentStyle: Int) {
        invalidateControlBarStates(modeComponentStyle = mode,
                style = textComponentStyle)
    }

    fun setEditorControlListener(editorControlListener: EditorControlListener?) {
        this.editorControlListener = editorControlListener
    }

    interface EditorControlListener {
        fun onInsertImageClicked()
        fun onInsertLinkClicked()
    }
}