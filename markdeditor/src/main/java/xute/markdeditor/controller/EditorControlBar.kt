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

    @TextHeadingStyle
    private var currentHeadingStyle = TextHeadingStyle.HEADING_NORMAL

    /**
     * For defining how many headings the user wants
     * the format is same as ModelComponentStyle
     * default is H5
     */
    private var maxHeading = TextHeadingStyle.HEADING_H5

    private var olEnabled = false
    private var ulEnabled = false

    @BlockQuoteStyle
    private var blockQuoteStyle = BlockQuoteStyle.QUOTE_NORMAL

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
            mEditor?.setHeading(formatType = TextFormatType.FORMAT_NORMAL, headingStyle = TextHeadingStyle.HEADING_NORMAL)
            invalidateControlBarStates(modeComponentStyle = TextModeType.MODE_PLAIN,
                    formatType = TextFormatType.FORMAT_NORMAL,
                    textHeadingStyle = TextHeadingStyle.HEADING_NORMAL)
        }

        viewBinding.headingBtn.setOnClickListener {
            onHeadingClick()
        }

        viewBinding.headingTextBtn.setOnClickListener {
            onHeadingClick()
        }

        //TODO: Would need revisiting !!
        viewBinding.blockQuoteBtn.setOnClickListener {
            blockQuoteStyle = blockQuoteStyle.nextBlockQuoteStyle()

            when (blockQuoteStyle) {
                BlockQuoteStyle.QUOTE_NORMAL -> {
                    //switch to normal
                    mEditor?.setHeading(headingStyle = TextHeadingStyle.HEADING_NORMAL,
                            formatType = TextFormatType.FORMAT_HEADER)

                    invalidateControlBarStates(modeComponentStyle = TextModeType.MODE_PLAIN,
                            formatType = TextFormatType.FORMAT_NORMAL,
                            textHeadingStyle = TextHeadingStyle.HEADING_NORMAL)
                }
                BlockQuoteStyle.QUOTE_ITALIC -> {
                    //blockQuote
                    mEditor?.changeToBlockQuote(BlockQuoteStyle.QUOTE_ITALIC)
                    invalidateControlBarStates(modeComponentStyle = TextModeType.MODE_PLAIN,
                            formatType = TextFormatType.FORMAT_QUOTE,
                            textHeadingStyle = TextHeadingStyle.HEADING_NORMAL)
                }

                BlockQuoteStyle.QUOTE_CENTER_H2 -> {
                    //blockQuote with H2 style
                    mEditor?.setHeading(formatType = TextFormatType.FORMAT_HEADER,
                            headingStyle = TextHeadingStyle.HEADING_H2)
                    invalidateControlBarStates(modeComponentStyle = TextModeType.MODE_PLAIN,
                            formatType = TextFormatType.FORMAT_QUOTE,
                            textHeadingStyle = TextHeadingStyle.HEADING_H2)
                }
            }
        }

        viewBinding.bulletBtn.setOnClickListener {
            if (olEnabled) {
                //switch to normal
                mEditor?.setHeading(TextFormatType.FORMAT_NORMAL, TextHeadingStyle.HEADING_NORMAL)
                invalidateControlBarStates(TextModeType.MODE_PLAIN, TextFormatType.FORMAT_LIST, TextHeadingStyle.HEADING_NORMAL)
                olEnabled = false
                ulEnabled = false
            } else if (ulEnabled) {
                // switch to ol mode
                mEditor?.changeToOLMode()
                invalidateControlBarStates(TextModeType.MODE_OL, TextFormatType.FORMAT_LIST, TextHeadingStyle.HEADING_NORMAL)
                olEnabled = true
                ulEnabled = false
            } else if (!olEnabled && !ulEnabled) {
                // switch to ul mode
                mEditor?.changeToULMode()
                invalidateControlBarStates(TextModeType.MODE_UL, TextFormatType.FORMAT_LIST, TextHeadingStyle.HEADING_NORMAL)
                ulEnabled = true
                olEnabled = false
            }
        }

        viewBinding.insertHrBtn.setOnClickListener { mEditor?.insertHorizontalDivider() }

        viewBinding.insertLinkBtn.setOnClickListener { editorControlListener?.onInsertLinkClicked() }

        viewBinding.insertImageBtn.setOnClickListener { editorControlListener?.onInsertImageClicked() }
    }

    private fun onHeadingClick() {
        currentHeadingStyle = currentHeadingStyle.nextHeadingStyle()
        val formatType: Int
        if (currentHeadingStyle == TextHeadingStyle.HEADING_NORMAL) {
            formatType = TextFormatType.FORMAT_NORMAL
            mEditor?.setHeading(formatType = formatType, headingStyle = TextHeadingStyle.HEADING_NORMAL)
        } else {
            formatType = TextFormatType.FORMAT_HEADER
            mEditor?.setHeading(formatType = formatType, headingStyle = currentHeadingStyle)
        }
        invalidateControlBarStates(modeComponentStyle = TextModeType.MODE_PLAIN,
                formatType = formatType,
                textHeadingStyle = currentHeadingStyle)
    }

    private fun enableNormalText(enabled: Boolean) {
        if (enabled) {
            viewBinding.normalTextBtn.setTextColor(enabledColor)
        } else {
            viewBinding.normalTextBtn.setTextColor(disabledColor)
        }
    }

    private fun setTextFormatType(@TextFormatType textFormatType: Int, @TextHeadingStyle textHeadingStyle: Int) {
        enableNewHeading(textFormatType)
        when (textFormatType) {
            TextFormatType.FORMAT_NORMAL -> {
                currentHeadingStyle = TextFormatType.FORMAT_NORMAL
                viewBinding.headingTextBtn.setTextColor(disabledColor)
                viewBinding.headingNumberBtn.setTextColor(disabledColor)
                viewBinding.headingNumberBtn.text = "$textHeadingStyle"
            }

            TextFormatType.FORMAT_QUOTE -> {
                enableBlockQuote(true)
                enableNormalText(false)
                enableBullet(enable = false, isOrdered = false)

                viewBinding.headingTextBtn.setTextColor(disabledColor)
                viewBinding.headingNumberBtn.setTextColor(disabledColor)
                viewBinding.headingNumberBtn.text = "$textHeadingStyle"
            }

            TextFormatType.FORMAT_HEADER -> {
                setTextHeadingStyle(textHeadingStyle = textHeadingStyle)
                enableNormalText(false)
                enableBullet(enable = false, isOrdered = false)
            }

            else -> {
            }
        }
    }

    private fun enableNewHeading(@TextHeadingStyle textComponentStyle: Int) {
        when (textComponentStyle) {
            TextHeadingStyle.HEADING_H1 -> {
                viewBinding.headingBtn.setImageResource(R.drawable.ic_h1)
            }

            TextHeadingStyle.HEADING_H2 -> {
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

    private fun enableBlockQuote(enable: Boolean) {
        if (enable) {
            when (blockQuoteStyle) {
                BlockQuoteStyle.QUOTE_NORMAL -> {
                    //switch to normal
                    viewBinding.blockQuoteBtn.setImageResource(R.drawable.ic_quote_0)
                }
                BlockQuoteStyle.QUOTE_ITALIC -> {
                    //blockQuote
                    viewBinding.blockQuoteBtn.setImageResource(R.drawable.ic_quote_1)
                }

                BlockQuoteStyle.QUOTE_CENTER_H2 -> {
                    //blockQuote with H2 style
                    viewBinding.blockQuoteBtn.setImageResource(R.drawable.ic_quote_2)
                }
            }
        } else {
            blockQuoteStyle = BlockQuoteStyle.QUOTE_NORMAL
            viewBinding.blockQuoteBtn.setImageResource(R.drawable.ic_quote_0)
        }
    }

    private fun invalidateControlBarStates(@TextModeType modeComponentStyle: Int,
                                           @TextFormatType formatType: Int,
                                           @TextHeadingStyle textHeadingStyle: Int) {
        when (modeComponentStyle) {
            TextModeType.MODE_OL -> {
                enableBlockQuote(false)
                setTextFormatType(formatType, textHeadingStyle)
                enableNormalText(false)
                enableBullet(enable = true, isOrdered = true)
            }
            TextModeType.MODE_UL -> {
                enableBlockQuote(false)
                setTextFormatType(formatType, textHeadingStyle)
                enableNormalText(false)
                enableBullet(enable = true, isOrdered = false)
            }

            TextModeType.MODE_PLAIN -> {
                setTextFormatType(formatType, textHeadingStyle)
            }
        }
    }

    private fun setTextHeadingStyle(@TextHeadingStyle textHeadingStyle: Int) {
        when (textHeadingStyle) {
            TextHeadingStyle.HEADING_NORMAL -> {
                enableBlockQuote(false)
                enableNormalText(true)
                enableBullet(enable = false, isOrdered = false)
            }
            TextHeadingStyle.HEADING_H1 -> {
                enableBlockQuote(false)
                enableNormalText(false)
                enableBullet(enable = false, isOrdered = false)
            }
            TextHeadingStyle.HEADING_H2 -> {
                enableBlockQuote(false)
                enableNormalText(false)
                enableBullet(enable = false, isOrdered = false)
            }
            TextHeadingStyle.HEADING_H3 -> {
                enableBlockQuote(false)
                enableNormalText(false)
                enableBullet(enable = false, isOrdered = false)
            }
            TextHeadingStyle.HEADING_H4 -> {
                enableBlockQuote(false)
                enableNormalText(false)
                enableBullet(enable = false, isOrdered = false)
            }
            TextHeadingStyle.HEADING_H5 -> {
                enableBlockQuote(false)
                enableNormalText(false)
                enableBullet(enable = false, isOrdered = false)
            }
        }

        viewBinding.headingTextBtn.setTextColor(enabledColor)
        viewBinding.headingNumberBtn.setTextColor(enabledColor)
        viewBinding.headingNumberBtn.text = "$textHeadingStyle"
    }

    override fun onFocusedViewHas(mode: Int, textFormatType: Int, textComponentStyle: Int) {
        invalidateControlBarStates(modeComponentStyle = mode,
                formatType = textFormatType,
                textHeadingStyle = textComponentStyle)
    }

    fun setEditorControlListener(editorControlListener: EditorControlListener?) {
        this.editorControlListener = editorControlListener
    }

    interface EditorControlListener {
        fun onInsertImageClicked()
        fun onInsertLinkClicked()
    }
}