package xute.markdeditor.components

import android.content.Context
import android.content.res.ColorStateList
import android.content.res.Resources
import android.graphics.Typeface
import android.text.Editable
import android.text.TextWatcher
import android.util.TypedValue
import android.view.KeyEvent
import android.view.View
import android.view.View.OnFocusChangeListener
import androidx.core.content.ContextCompat
import xute.markdeditor.R
import xute.markdeditor.models.ComponentTag
import xute.markdeditor.models.TextComponentModel
import xute.markdeditor.styles.TextComponentStyle
import xute.markdeditor.utilities.FontSize

class TextComponent(private val mContext: Context, private val textComponentCallback: TextComponentCallback?) {
    private val r: Resources by lazy { mContext.resources }
    private var spaceExist = false
    private var defaultColorState: ColorStateList? = null

    /**
     * Method to create new instance according to mode provided.
     * Mode can be [PLAIN, UL, OL]
     * @param mode mode of new TextComponent.
     * @return new instance of TextComponent.
     */
    fun newTextComponent(mode: Int): TextComponentView {
        val customInputView = TextComponentView(mContext, mode)
        val et = customInputView.inputBox
        et.setImeActionLabel("Enter", KeyEvent.KEYCODE_ENTER)
        et.setOnKeyListener(View.OnKeyListener { view, keyCode, keyEvent ->
            if (keyEvent.action != KeyEvent.ACTION_DOWN) return@OnKeyListener true
            if (keyCode == KeyEvent.KEYCODE_DEL) {
                textComponentCallback?.onRemoveTextComponent((customInputView.tag as ComponentTag).componentIndex)
            }
            false
        })
        et.onFocusChangeListener = OnFocusChangeListener { view, inFocus ->
            if (inFocus) {
                textComponentCallback?.onFocusGained(customInputView)
            }
        }
        et.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun onTextChanged(charSequence: CharSequence, start: Int, before: Int, count: Int) {
                val clen = charSequence.length
                if (clen > 0) {
                    val ch = charSequence[charSequence.length - 1]
                    if (isSpaceCharacter(ch) && before < count) {
                        if (spaceExist) {
                            var newString = charSequence.toString().trim { it <= ' ' }
                            newString = String.format("%s ", newString)
                            et.setText(newString)
                            et.setSelection(newString.length)
                        }
                        spaceExist = true
                    } else {
                        spaceExist = false
                    }
                    val sequenceToCheckNewLineCharacter =
                            /*if (clen > 1) charSequence.subSequence(clen - 2, clen).toString()
                            else*/ charSequence.subSequence(clen - 1, clen).toString()

                    /*var shouldInsertNext = false
                    (customInputView.tag as? ComponentTag)?.let { componentTag ->
                        (componentTag.component as? TextComponentModel)?.let { textComponentModel ->
                            //set data in model
                            if (textComponentModel.textStyle in TextComponentStyle.FORMAT_NORMAL..TextComponentStyle.QUOTE_H3_LIGHT)
                                shouldInsertNext = true
                        }
                    }*/

                    val noReadableCharactersAfterCursor = sequenceToCheckNewLineCharacter.trim { it <= ' ' }.isEmpty() /*|| shouldInsertNext*/
                    //if last characters are [AB\n<space>] or [AB\n] then we insert new TextComponent
                    //else if last characters are [AB\nC] ignore the insert.
                    if (sequenceToCheckNewLineCharacter.contains("\n") && noReadableCharactersAfterCursor) {
                        //If last characters are like [AB\n ] then new sequence will be [AB]
                        // i.e leave 2 characters from end.
                        //else if last characters are like [AB\n] then also new sequence will be [AB]
                        //but we need to leave 1 character from end.
                        val newSequence = if (sequenceToCheckNewLineCharacter.length > 1) charSequence.subSequence(0, clen - 2) else charSequence.subSequence(0, clen - 1)
                        et.setText(newSequence)
                        textComponentCallback?.onInsertTextComponent((customInputView.tag as ComponentTag).componentIndex)
                    }
                }
            }

            override fun afterTextChanged(editable: Editable) {}
        })
        return customInputView
    }

    private fun isSpaceCharacter(ch: Char): Boolean {
        return ch == ' '
    }

    /**
     * updates view with latest style info.
     * @param textComponentView to be updated.
     */
    fun updateComponent(textComponentView: TextComponentView) {
        val componentTag = textComponentView.tag as ComponentTag
        //get style format type
        when (val componentStyle = (componentTag.component as TextComponentModel).textStyle) {
            TextComponentStyle.FORMAT_NORMAL -> {
                setNormalInput(textComponentView, componentStyle)
            }

            in TextComponentStyle.HEADING_H1..TextComponentStyle.HEADING_H5 -> {
                textComponentView.apply {
                    defaultColorState?.let {
                        inputBox.setTextColor(it)
                    }
                    inputBox.textSize = FontSize.getFontSize(componentStyle).toFloat()
                    inputBox.setTypeface(null, Typeface.BOLD)
                    inputBox.setBackgroundResource(R.drawable.text_input_bg)
                    inputBox.setPadding(
                            dpToPx(16), //left
                            dpToPx(8),  //top
                            dpToPx(16), //right
                            dpToPx(8)   //bottom
                    )
                    inputBox.setLineSpacing(2f, 1.1f)
                }
            }

            TextComponentStyle.QUOTE_ITALIC -> {
                textComponentView.apply {
                    defaultColorState?.let {
                        inputBox.setTextColor(it)
                    }
                    inputBox.textSize = FontSize.getFontSize(componentStyle).toFloat()
                    inputBox.setTypeface(null, Typeface.ITALIC)
                    inputBox.setBackgroundResource(R.drawable.blockquote_component_bg)
                    inputBox.setPadding(
                            dpToPx(16), //left
                            dpToPx(2),  //top
                            dpToPx(16), //right
                            dpToPx(2)   //bottom
                    )
                    inputBox.setLineSpacing(2f, 1.2f)
                }
            }

            TextComponentStyle.QUOTE_H3_LIGHT -> {
                textComponentView.apply {
                    defaultColorState = inputBox.textColors //save original colors
                    inputBox.setTextColor(ContextCompat.getColor(mContext, R.color.disabled))

                    inputBox.textSize = FontSize.getFontSize(TextComponentStyle.HEADING_H3).toFloat()
                    inputBox.setTypeface(null, Typeface.NORMAL)
                    inputBox.setBackgroundResource(R.drawable.text_input_bg)
                    inputBox.setPadding(
                            dpToPx(20), //left
                            dpToPx(10),  //top
                            dpToPx(20), //right
                            dpToPx(10)   //bottom
                    )
                    inputBox.setLineSpacing(2f, 1.1f)
                }
            }

            else -> {
                setNormalInput(textComponentView, componentStyle)
            }
        }
    }

    private fun setNormalInput(textComponentView: TextComponentView, @TextComponentStyle componentStyle: Int) {
        textComponentView.apply {
            defaultColorState?.let {
                inputBox.setTextColor(it)
            }
            inputBox.textSize = FontSize.getFontSize(componentStyle).toFloat()
            inputBox.setTypeface(null, Typeface.NORMAL)
            inputBox.setBackgroundResource(R.drawable.text_input_bg)
            inputBox.setPadding(
                    dpToPx(16), //left
                    dpToPx(4),  //top
                    dpToPx(16), //right
                    dpToPx(4)   //bottom
            )
        }
    }

    /**
     * Convert dp to px value.
     * @param dp value
     * @return pixel value of given dp.
     */
    private fun dpToPx(dp: Int): Int {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, dp.toFloat(), r.displayMetrics).toInt()
    }

    interface TextComponentCallback {
        fun onInsertTextComponent(selfIndex: Int)
        fun onFocusGained(view: View?)
        fun onRemoveTextComponent(selfIndex: Int)
    }

}