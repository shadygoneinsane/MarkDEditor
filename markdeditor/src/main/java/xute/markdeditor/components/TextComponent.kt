package xute.markdeditor.components

import android.content.Context
import android.content.res.Resources
import android.graphics.Typeface
import android.text.Editable
import android.text.TextWatcher
import android.util.TypedValue
import android.view.KeyEvent
import android.view.View
import android.view.View.OnFocusChangeListener
import xute.markdeditor.R
import xute.markdeditor.models.ComponentTag
import xute.markdeditor.models.TextComponentModel
import xute.markdeditor.styles.TextComponentStyle
import xute.markdeditor.utilities.FontSize

class TextComponent(private val mContext: Context, private val textComponentCallback: TextComponentCallback?) {
    private val r: Resources by lazy { mContext.resources }
    private var spaceExist = false

    /**
     * Method to create new instance according to mode provided.
     * Mode can be [PLAIN, UL, OL]
     * @param mode mode of new TextComponent.
     * @return new instance of TextComponent.
     */
    fun newTextComponent(mode: Int): TextComponentView {
        val customInput = TextComponentView(mContext, mode)
        val et = customInput.inputBox
        et.setImeActionLabel("Enter", KeyEvent.KEYCODE_ENTER)
        et.setOnKeyListener(View.OnKeyListener { view, keyCode, keyEvent ->
            if (keyEvent.action != KeyEvent.ACTION_DOWN) return@OnKeyListener true
            if (keyCode == KeyEvent.KEYCODE_DEL) {
                textComponentCallback?.onRemoveTextComponent((customInput.tag as ComponentTag).componentIndex)
            }
            false
        })
        et.onFocusChangeListener = OnFocusChangeListener { view, inFocus ->
            if (inFocus) {
                textComponentCallback?.onFocusGained(customInput)
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
                            if (clen > 1) charSequence.subSequence(clen - 2, clen).toString()
                            else charSequence.subSequence(clen - 1, clen).toString()
                    val noReadableCharactersAfterCursor = sequenceToCheckNewLineCharacter.trim { it <= ' ' }.isEmpty()
                    //if last characters are [AB\n<space>] or [AB\n] then we insert new TextComponent
                    //else if last characters are [AB\nC] ignore the insert.
                    if (sequenceToCheckNewLineCharacter.contains("\n") && noReadableCharactersAfterCursor) {
                        //If last characters are like [AB\n ] then new sequence will be [AB]
                        // i.e leave 2 characters from end.
                        //else if last characters are like [AB\n] then also new sequence will be [AB]
                        //but we need to leave 1 character from end.
                        val newSequence = if (sequenceToCheckNewLineCharacter.length > 1) charSequence.subSequence(0, clen - 2) else charSequence.subSequence(0, clen - 1)
                        et.setText(newSequence)
                        textComponentCallback?.onInsertTextComponent((customInput.tag as ComponentTag).componentIndex)
                    }
                }
            }

            override fun afterTextChanged(editable: Editable) {}
        })
        return customInput
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
        //get format type
        val componentStyle = (componentTag.component as TextComponentModel).textStyle

        when (componentStyle) {
            TextComponentStyle.FORMAT_NORMAL -> {
                setNormalInput(textComponentView)
            }

            in TextComponentStyle.HEADING_H1..TextComponentStyle.HEADING_H5 -> {
                textComponentView.inputBox.textSize = FontSize.getFontSize(componentStyle).toFloat()
                textComponentView.apply {
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

            /*TextComponentModel.QUOTE_H2_CENTER -> {
                textComponentView.apply {
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
            }*/

            else -> {
                setNormalInput(textComponentView)
            }
        }
    }

    private fun setNormalInput(view: TextComponentView) {
        view.apply {
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