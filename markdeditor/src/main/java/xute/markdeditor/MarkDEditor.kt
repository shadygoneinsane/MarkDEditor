package xute.markdeditor

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.inputmethod.InputMethodManager
import xute.markdeditor.components.*
import xute.markdeditor.components.ImageComponentItem.ImageComponentListener
import xute.markdeditor.components.TextComponent.TextComponentCallback
import xute.markdeditor.models.ComponentTag
import xute.markdeditor.models.DraftModel
import xute.markdeditor.models.ImageComponentModel
import xute.markdeditor.models.TextComponentModel
import xute.markdeditor.styles.BlockQuoteStyle
import xute.markdeditor.styles.TextFormatType
import xute.markdeditor.styles.TextHeadingStyle
import xute.markdeditor.styles.TextModeType
import xute.markdeditor.utilities.ComponentMetadataHelper.getNewComponentTag
import xute.markdeditor.utilities.DraftManager
import xute.markdeditor.utilities.MarkDownConverter
import xute.markdeditor.utilities.RenderingUtils
import java.util.*

class MarkDEditor(context: Context, attrs: AttributeSet?) : MarkDCore(context, attrs), TextComponentCallback, ImageComponentListener {
    private var _activeView: View? = null
    private var mContext: Context? = null
    private var draftManager: DraftManager? = null
    private var __textComponent: TextComponent? = null
    private var __imageComponent: ImageComponent? = null
    private var __horizontalComponent: HorizontalDividerComponent? = null

    private lateinit var markDownConverter: MarkDownConverter
    private var serverToken: String? = null
    private var renderingUtils: RenderingUtils? = null
    private var editorFocusReporter: EditorFocusReporter? = null
    private var startHintText: String? = null

    private var defaultHeadingStyle = TextHeadingStyle.HEADING_NORMAL

    private var isFreshEditor = false
    private var oldDraft: DraftModel? = null

    private fun init(context: Context) {
        mContext = context
        draftManager = DraftManager()
        bulletGroupModels = ArrayList()
        markDownConverter = MarkDownConverter()
        //TODO : maybe it'll crash here !!
        setCurrentInputMode(TextModeType.MODE_PLAIN)
        __textComponent = TextComponent(context, this)
        __imageComponent = ImageComponent(context)
        __horizontalComponent = HorizontalDividerComponent(context)
    }

    /**
     * Helper method to configure editor
     *
     * @param serverUrl
     * @param serverToken
     * @param isDraft
     * @param startHint
     * @param defaultHeadingType
     */
    fun configureEditor(serverUrl: String?, serverToken: String?, isDraft: Boolean, startHint: String?, defaultHeadingType: Int) {
        this.serverToken = serverToken
        Companion.serverUrl = serverUrl
        startHintText = startHint
        this.defaultHeadingStyle = defaultHeadingType
        if (!isDraft) {
            startFreshEditor()
        }
    }

    /**
     * Inserts single text component
     */
    private fun startFreshEditor() {
        //starts basic editor with single text component.
        isFreshEditor = true
        addTextComponent(0)
        setHeading(formatType = TextFormatType.FORMAT_HEADER, headingStyle = defaultHeadingStyle)
    }

    /**
     * adds new TextComponent.
     *
     * @param insertIndex at which addition of new [TextComponent] take place.
     */
    private fun addTextComponent(insertIndex: Int) {
        __textComponent?.let { textComponent ->
            //setting default mode as plain
            val textComponentItem = textComponent.newTextComponent(TextModeType.MODE_PLAIN)
            //prepare tag
            val textComponentModel = TextComponentModel()
            if (insertIndex == 0) {
                if (startHintText != null && isFreshEditor) {
                    textComponentItem.setHintText(startHintText)
                }
            }
            val componentTag = getNewComponentTag(insertIndex)
            componentTag.component = textComponentModel
            textComponentItem.tag = componentTag
            addView(textComponentItem, insertIndex)
            textComponent.updateComponent(textComponentItem)
            setFocus(textComponentItem)
            reComputeTagsAfter(insertIndex)
            refreshViewOrder()
        }
    }

    /**
     * adds new TextComponent with pre-filled text.
     *
     * @param insertIndex at which addition of new textcomponent take place.
     */
    fun addTextComponent(insertIndex: Int, content: String?) {
        __textComponent?.let { textComponent ->
            val textComponentItem = textComponent.newTextComponent(TextModeType.MODE_PLAIN)
            //prepare tag
            val textComponentModel = TextComponentModel()
            val componentTag = getNewComponentTag(insertIndex)
            componentTag.component = textComponentModel
            textComponentItem.tag = componentTag
            textComponentItem.setText(content)
            addView(textComponentItem, insertIndex)
            __textComponent?.updateComponent(textComponentItem)
            setFocus(textComponentItem)
            reComputeTagsAfter(insertIndex)
            refreshViewOrder()
        }
    }

    /**
     * sets heading to text component
     *
     * @param headingStyle number to be set
     */
    fun setHeading(@TextFormatType formatType: Int = TextFormatType.FORMAT_NORMAL, @TextHeadingStyle headingStyle: Int) {
        setDataInView(TextModeType.MODE_PLAIN, formatType, headingStyle)
        refreshViewOrder()
    }

    fun setDataInView(@TextModeType textModeType: Int,
                      @TextFormatType formatType: Int,
                      @TextHeadingStyle headingStyle: Int? = null) {
        (_activeView as? TextComponentItem)?.let { textComponentItem ->
            //set mode as plain since we are defining it as a heading format type
            textComponentItem.setMode(textModeType)

            //set tag information
            (textComponentItem.tag as? ComponentTag)?.let { componentTag ->
                (componentTag.component as? TextComponentModel)?.let { textComponentModel ->

                    //set data in model
                    textComponentModel.textFormatType = formatType
                    headingStyle?.let { heading ->
                        textComponentModel.textHeadingStyle = heading
                    }
                }
            }
            __textComponent?.updateComponent(textComponentItem)
        }
    }

    fun setCurrentInputMode(@TextModeType textModeType: Int) {
        (_activeView as? TextComponentItem)?.let { textComponentItem ->
            //set mode as plain since we are defining it as a heading format type
            textComponentItem.setMode(textModeType)
            __textComponent?.updateComponent(textComponentItem)
        }
    }

    /**
     * @param view to be focused on.
     */
    private fun setFocus(view: View) {
        _activeView = view
        (_activeView as? TextComponentItem)?.let { textComponentItem ->
            val currentInputListMode = textComponentItem.getMode()
            (view as? TextComponentItem)?.inputBox?.requestFocus()
            reportStylesOfFocusedView(textComponentItem)
        }
    }

    /**
     * re-compute the indexes of view after a view is inserted/deleted.
     *
     * @param startIndex index after which re-computation will be done.
     */
    private fun reComputeTagsAfter(startIndex: Int) {
        var _child: View
        for (i in startIndex until childCount) {
            _child = getChildAt(i)
            val componentTag = _child.tag as ComponentTag
            componentTag.componentIndex = i
            _child.tag = componentTag
        }
    }

    /**
     * method to send callback for focussed view back to subscriber(if any).
     *
     * @param view newly focus view.
     */
    private fun reportStylesOfFocusedView(view: TextComponentItem) {
        if (editorFocusReporter != null) {
            editorFocusReporter?.onFocusedViewHas(view.getMode(), view.getTextFormatType(), view.getTextHeadingStyle())
        }
    }

    fun loadDraft(draft: DraftModel) {
        oldDraft = draft
        val contents = draft.items
        if (contents != null) {
            if (contents.size > 0) {
                renderingUtils = RenderingUtils()
                renderingUtils?.setEditor(this)
                renderingUtils?.render(contents)
            } else {
                startFreshEditor()
            }
        } else {
            startFreshEditor()
        }
    }

    override fun onInsertTextComponent(selfIndex: Int) {
        addTextComponent(selfIndex + 1)
    }

    override fun onFocusGained(view: View?) {
        view?.let { setFocus(it) }
    }

    /**
     * This callback method removes view at given index.
     * It checks if there is a horizontal line just before it, it removes the line too.
     * Else it removes the current view only.
     *
     * @param selfIndex index of view to remove.
     */
    override fun onRemoveTextComponent(selfIndex: Int) {
        if (selfIndex == 0) return
        val viewToBeRemoved = getChildAt(selfIndex)
        val previousView = getChildAt(selfIndex - 1)
        val content = (viewToBeRemoved as TextComponentItem).inputBox.text.toString()
        if (previousView is HorizontalDividerComponentItem) {
            //remove previous view.
            removeViewAt(selfIndex - 1)
            reComputeTagsAfter(selfIndex - 1)
            //focus on latest text component
            val lastTextComponent = getLatestTextComponentIndexBefore(selfIndex - 1)
            setFocus(getChildAt(lastTextComponent))
        } else if (previousView is TextComponentItem) {
            removeViewAt(selfIndex)
            val contentLen = previousView.inputBox.text.toString().length
            previousView.inputBox.append(String.format("%s", content))
            setFocus(previousView, contentLen)
        } else if (previousView is ImageComponentItem) {
            setActiveView(previousView)
            previousView.setFocus()
        }
        reComputeTagsAfter(selfIndex)
        refreshViewOrder()
    }

    /**
     * This method searches whithin view group for a TextComponent which was
     * inserted prior to startIndex.
     *
     * @param starIndex index from which search starts.
     * @return index of LatestTextComponent before startIndex.
     */
    private fun getLatestTextComponentIndexBefore(starIndex: Int): Int {
        var view: View? = null
        for (i in starIndex downTo 0) {
            view = getChildAt(i)
            if (view is TextComponentItem) return i
        }
        return 0
    }

    /**
     * overloaded method for focusing view, it puts the cursor at specified position.
     *
     * @param view to be focused on.
     */
    private fun setFocus(view: View, cursorPos: Int) {
        _activeView = view
        view.requestFocus()
        if (view is TextComponentItem) {
            val mgr = mContext?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            mgr.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)
            //move cursor
            view.inputBox.setSelection(cursorPos)
            reportStylesOfFocusedView(view)
        }
    }

    private fun setActiveView(view: View) {
        _activeView = view
    }

    /**
     * adds link.
     *
     * @param text link text
     * @param url  linking url.
     */
    fun addLink(text: String?, url: String?) {
        if (_activeView is TextComponentItem) {
            val stringBuilder = StringBuilder()
            stringBuilder
                    .append(" <a href=\"")
                    .append(url)
                    .append("\">")
                    .append(text)
                    .append("</a> ")
            (_activeView as TextComponentItem).inputBox.append(stringBuilder.toString())
        }
    }

    /**
     * changes the current text into block quote.
     */
    fun changeToBlockQuote(@BlockQuoteStyle blockQuoteStyle: Int,
                           @TextFormatType formatType: Int = TextFormatType.FORMAT_QUOTE,
                           @TextModeType modeType: Int = TextModeType.MODE_PLAIN) {
        //setCurrentInputType(formatType)
        setCurrentInputMode(TextModeType.MODE_PLAIN)

        if (_activeView is TextComponentItem) {
            (_activeView as TextComponentItem).setMode(modeType)
            val componentTag = (_activeView as TextComponentItem).tag as ComponentTag
            (componentTag.component as? TextComponentModel)?.textFormatType = TextFormatType.FORMAT_QUOTE
            __textComponent!!.updateComponent(_activeView as TextComponentItem)
        }
        refreshViewOrder()
    }

    /**
     * change the current insert mode to Ordered List Mode.
     * Increasing numbers are used to denote each item.
     */
    fun changeToOLMode() {
        val currentInputListMode = TextModeType.MODE_OL
        (_activeView as? TextComponentItem)?.let { textComponentItem ->
            textComponentItem.setMode(currentInputListMode)
            val componentTag = textComponentItem.tag as ComponentTag
            (componentTag.component as? TextComponentModel)?.textFormatType = TextFormatType.FORMAT_NORMAL
            __textComponent?.updateComponent(_activeView as TextComponentItem)
        }
        refreshViewOrder()
    }

    /**
     * change the current insert mode to UnOrdered List Mode.
     * Circular filled bullets are used to denote each item.
     */
    fun changeToULMode() {
        val currentInputListMode = TextModeType.MODE_UL
        if (_activeView is TextComponentItem) {
            (_activeView as TextComponentItem).setMode(currentInputListMode)
            val componentTag = _activeView?.tag as ComponentTag
            (componentTag.component as TextComponentModel?)?.textFormatType = TextFormatType.FORMAT_NORMAL
            __textComponent?.updateComponent(_activeView as TextComponentItem)
        }
        refreshViewOrder()
    }

    /**
     * This method gets the suitable insert index using
     * `checkInvalidateAndCalculateInsertIndex()` method.
     * Prepares the ImageComponent and inserts it.
     * Since the user might need to type further, it inserts new TextComponent below
     * it.
     *
     * @param filePath uri of image to be inserted.
     */
    fun insertImage(filePath: String?) {
        var insertIndex = checkInvalidateAndCalculateInsertIndex()
        val imageComponentItem = __imageComponent!!.getNewImageComponentItem(this)
        //prepare tag
        val imageComponentModel = ImageComponentModel()
        val imageComponentTag = getNewComponentTag(insertIndex)
        imageComponentTag.component = imageComponentModel
        imageComponentItem.tag = imageComponentTag
        imageComponentItem.setImageInformation(filePath!!, serverToken, false, "")
        addView(imageComponentItem, insertIndex)
        reComputeTagsAfter(insertIndex)
        refreshViewOrder()
        //add another text component below image
        insertIndex++
        setCurrentInputMode(TextModeType.MODE_PLAIN)
        addTextComponent(insertIndex)
    }

    /**
     * This method checks the current active/focussed view.
     * If there is some text in it, then next insertion will take place below this
     * view.
     * Else the current focussed view will be removed and new view will inserted
     * at its position.
     *
     * @return index of next insert.
     */
    private fun checkInvalidateAndCalculateInsertIndex(): Int {
        if (_activeView == null) return 0
        val tag = _activeView?.tag as ComponentTag
        val activeIndex = tag.componentIndex
        val view = getChildAt(activeIndex)
        //check for TextComponentItem
        return if (view is TextComponentItem) {
            //if active text component has some texts.
            if (view.inputBox.text.isNotEmpty()) {
                //insert below it
                activeIndex + 1
            } else {
                //remove current view
                removeViewAt(activeIndex)
                reComputeTagsAfter(activeIndex)
                refreshViewOrder()
                //insert at the current position.
                activeIndex
            }
        } else activeIndex + 1
    }

    /**
     * This method gets the suitable insert index using
     * `checkInvalidateAndCalculateInsertIndex()` method.
     * Prepares the ImageComponent and inserts it.
     * loads already uploaded image and sets caption
     *
     * @param filePath uri of image to be inserted.
     */
    fun insertImage(insertIndex: Int, filePath: String?, uploaded: Boolean, caption: String?) {
        val imageComponentItem = __imageComponent!!.getNewImageComponentItem(this)
        //prepare tag
        val imageComponentModel = ImageComponentModel()
        val imageComponentTag = getNewComponentTag(insertIndex)
        imageComponentTag.component = imageComponentModel
        imageComponentItem.tag = imageComponentTag
        imageComponentItem.setImageInformation(filePath!!, "", uploaded, caption)
        addView(imageComponentItem, insertIndex)
        reComputeTagsAfter(insertIndex)
    }

    /**
     * Inserts new horizontal ruler.
     */
    fun insertHorizontalDivider() {
        var insertIndex = nextIndex
        val horizontalDividerComponentItem = __horizontalComponent!!.newHorizontalComponentItem
        val _hrTag = getNewComponentTag(insertIndex)
        horizontalDividerComponentItem.tag = _hrTag
        addView(horizontalDividerComponentItem, insertIndex)
        reComputeTagsAfter(insertIndex)
        //add another text component below image
        insertIndex++
        setCurrentInputMode(TextModeType.MODE_PLAIN)
        addTextComponent(insertIndex)
        refreshViewOrder()
    }

    /**
     * @return index next to focused view.
     */
    private val nextIndex: Int
        get() {
            val tag = _activeView?.tag as ComponentTag
            return tag.componentIndex + 1
        }

    /**
     * Inserts new horizontal ruler.
     * Adds new text components based on passed parameter.
     */
    fun insertHorizontalDivider(insertNewTextComponentAfterThis: Boolean) {
        var insertIndex = nextIndex
        val horizontalDividerComponentItem = __horizontalComponent!!.newHorizontalComponentItem
        val _hrTag = getNewComponentTag(insertIndex)
        horizontalDividerComponentItem.tag = _hrTag
        addView(horizontalDividerComponentItem, insertIndex)
        reComputeTagsAfter(insertIndex)
        //add another text component below image
        if (insertNewTextComponentAfterThis) {
            insertIndex++
            setCurrentInputMode(TextModeType.MODE_PLAIN)
            addTextComponent(insertIndex)
        } else {
            setFocus(horizontalDividerComponentItem)
        }
        refreshViewOrder()
    }

    override fun onImageRemove(removeIndex: Int) {
        if (removeIndex == 0) {
            //insert 1 text component
            removeViewAt(0)
            addTextComponent(0)
        } else {
            removeViewAt(removeIndex)
        }
        reComputeTagsAfter(removeIndex)
        refreshViewOrder()
    }

    override fun onExitFromCaptionAndInsertNewTextComponent(currentIndex: Int) {
        addTextComponent(currentIndex)
    }

    /**
     * @return markdown format of editor content.
     */
    val markdownContent: String
        get() = if (markDownConverter.isDataProcessed) {
            markDownConverter.markDown
        } else {
            markDownConverter.processData(this).markDown
        }

    /**
     * @return List of Draft Content.
     */
    fun getDraft(): DraftModel? {
        val newDraft = draftManager?.processDraftContent(this)
        oldDraft?.let {
            newDraft?.draftId = it.draftId
        } ?: run {
            newDraft?.draftId = System.currentTimeMillis()
        }
        return newDraft
    }

    /**
     * @return list of images inserted.
     */
    fun imageList(): List<String?> {
        return if (markDownConverter.isDataProcessed) {
            markDownConverter.getImages()
        } else {
            markDownConverter.processData(this).getImages()
        }
    }

    /**
     * setter method to subscribe for listening to focus change.
     *
     * @param editorFocusReporter callback for editor focus.
     */
    fun setEditorFocusReporter(editorFocusReporter: EditorFocusReporter?) {
        this.editorFocusReporter = editorFocusReporter
    }

    interface EditorFocusReporter {
        fun onFocusedViewHas(@TextModeType mode: Int, @TextFormatType textFormatType: Int, @TextHeadingStyle textComponentStyle: Int)
    }

    companion object {
        var serverUrl: String? = null
    }

    init {
        init(context)
    }
}