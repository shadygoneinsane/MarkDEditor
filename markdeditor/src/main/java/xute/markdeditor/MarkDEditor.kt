package xute.markdeditor

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.annotation.Nullable
import xute.markdeditor.components.*
import xute.markdeditor.components.ImageComponentItem.ImageComponentListener
import xute.markdeditor.components.TextComponent.TextComponentCallback
import xute.markdeditor.models.ComponentTag
import xute.markdeditor.models.DraftModel
import xute.markdeditor.models.ImageComponentModel
import xute.markdeditor.models.TextComponentModel
import xute.markdeditor.styles.TextComponentStyle
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

    private var defaultHeadingStyle = TextComponentStyle.FORMAT_NORMAL

    /**
     * Input List Mode
     */
    private var currentInputMode = TextModeType.MODE_PLAIN

    private var isFreshEditor = false
    private var oldDraft: DraftModel? = null

    private fun init(context: Context) {
        mContext = context
        draftManager = DraftManager()
        bulletGroupModels = ArrayList()
        markDownConverter = MarkDownConverter()
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
        setHeading(componentStyle = TextComponentStyle.FORMAT_NORMAL)
    }

    /**
     * adds new TextComponent (with pre-filled text - if exists)
     * @param content to be added if its not null
     * @param insertIndex at which addition of new [TextComponent] take place.
     */
    public fun addTextComponent(insertIndex: Int, @Nullable content: String? = null) {
        __textComponent?.let { textComponent ->
            //setting default mode as plain
            val textComponentItem = textComponent.newTextComponent(currentInputMode)
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

            //if text exists add to view
            content?.let {
                textComponentItem.setText(it)
            }
            addView(textComponentItem, insertIndex)
            textComponent.updateComponent(textComponentItem)
            setFocus(textComponentItem)
            reComputeTagsAfter(insertIndex)
            refreshViewOrder()
        }
    }

    /**
     * sets heading to text component
     *
     * @param componentStyle number to be set
     */
    fun setHeading(@TextComponentStyle componentStyle: Int) {
        setDataInView(TextModeType.MODE_PLAIN, componentStyle)
        refreshViewOrder()
    }

    fun setDataInView(@TextModeType textModeType: Int,
                      @TextComponentStyle componentStyle: Int) {
        setCurrentInputMode(textModeType)
        (_activeView as? TextComponentView)?.let { textComponentItem ->
            //set mode as plain since we are defining it as a heading format type
            textComponentItem.setMode(textModeType)

            //set tag information
            (textComponentItem.tag as? ComponentTag)?.let { componentTag ->
                (componentTag.component as? TextComponentModel)?.let { textComponentModel ->

                    //set data in model
                    textComponentModel.textStyle = componentStyle
                }
            }
            __textComponent?.updateComponent(textComponentItem)
        }
    }

    fun setCurrentInputMode(@TextModeType modeType: Int): Int {
        currentInputMode = modeType
        return currentInputMode
    }

    /**
     * @param view to be focused on.
     */
    private fun setFocus(view: View) {
        _activeView = view
        (_activeView as? TextComponentView)?.let { textComponentItem ->
            setCurrentInputMode(textComponentItem.getMode())
            (view as? TextComponentView)?.inputBox?.requestFocus()
            reportStylesOfFocusedView(textComponentItem)
        }
    }

    /**
     * re-compute the indexes of view after a view is inserted/deleted.
     *
     * @param startIndex index after which re-computation will be done.
     */
    private fun reComputeTagsAfter(startIndex: Int) {
        Log.d("vikesh", "Recomputing index")
        for (i in startIndex until childCount) {
            val child: View = getChildAt(i)
            val componentTag = child.tag as ComponentTag
            componentTag.componentIndex = i
            child.tag = componentTag
            Log.d("vikesh", "componentTag $componentTag")
        }
    }

    /**
     * method to send callback for focused view back to subscriber(if any).
     *
     * @param view newly focus view.
     */
    private fun reportStylesOfFocusedView(view: TextComponentView) {
        if (editorFocusReporter != null) {
            editorFocusReporter?.onFocusedViewHas(view.getMode(), view.getTextComponentStyle())
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
        val content = (viewToBeRemoved as TextComponentView).inputBox.text.toString()
        if (previousView is HorizontalDividerComponentItem) {
            //remove previous view.
            removeViewAt(selfIndex - 1)
            reComputeTagsAfter(selfIndex - 1)
            //focus on latest text component
            val lastTextComponent = getLatestTextComponentIndexBefore(selfIndex - 1)
            setFocus(getChildAt(lastTextComponent))
        } else if (previousView is TextComponentView) {
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
            if (view is TextComponentView) return i
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
        if (view is TextComponentView) {
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
        if (_activeView is TextComponentView) {
            val stringBuilder = StringBuilder()
            stringBuilder
                    .append(" <a href=\"")
                    .append(url)
                    .append("\">")
                    .append(text)
                    .append("</a> ")
            (_activeView as? TextComponentView)?.inputBox?.append(stringBuilder.toString())
        }
    }

    /**
     * changes the current text into block quote.
     */
    fun changeToBlockQuote(@TextModeType modeType: Int = TextModeType.MODE_PLAIN,
                           @TextComponentStyle componentStyle: Int = TextComponentStyle.QUOTE_ITALIC) {
        (_activeView as? TextComponentView)?.let { textComponentItem ->
            textComponentItem.setMode(setCurrentInputMode(modeType))
            val componentTag = textComponentItem.tag as ComponentTag
            (componentTag.component as? TextComponentModel)?.textStyle = componentStyle

            __textComponent?.updateComponent(textComponentItem)
        }
        refreshViewOrder()
    }

    /**
     * change the current insert mode to Ordered List Mode.
     * Increasing numbers are used to denote each item.
     */
    fun changeToOLMode() {
        (_activeView as? TextComponentView)?.let { textComponentItem ->
            textComponentItem.setMode(setCurrentInputMode(TextModeType.MODE_OL))
            val componentTag = textComponentItem.tag as ComponentTag
            (componentTag.component as? TextComponentModel)?.textStyle = TextComponentStyle.FORMAT_NORMAL
            __textComponent?.updateComponent(_activeView as TextComponentView)
        }
        refreshViewOrder()
    }

    /**
     * change the current insert mode to UnOrdered List Mode.
     * Circular filled bullets are used to denote each item.
     */
    fun changeToULMode() {
        (_activeView as? TextComponentView)?.let { textComponentItem ->
            textComponentItem.setMode(setCurrentInputMode(TextModeType.MODE_UL))
            val componentTag = _activeView?.tag as ComponentTag
            (componentTag.component as TextComponentModel?)?.textStyle = TextComponentStyle.FORMAT_NORMAL

            __textComponent?.updateComponent(textComponentItem)
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
    fun insertImage(filePath: String) {
        var insertIndex = checkInvalidateAndCalculateInsertIndex()
        val imageComponentItem = __imageComponent?.getNewImageComponentItem(this)
        //prepare tag
        val imageComponentModel = ImageComponentModel()
        val imageComponentTag = getNewComponentTag(insertIndex)
        imageComponentTag.component = imageComponentModel
        imageComponentItem?.tag = imageComponentTag
        imageComponentItem?.setImageInformation(filePath, serverToken, false, "")
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
        return if (view is TextComponentView) {
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
     * Adds new text components based on passed parameter.
     */
    fun insertHorizontalDivider(insertNewTextComponentAfterThis: Boolean? = null) {
        var insertIndex = nextIndex
        __horizontalComponent?.let { horizontalDividerComponent ->
            val horizontalDividerComponentItem = horizontalDividerComponent.newHorizontalComponentItem()
            val hrTag = getNewComponentTag(insertIndex)
            horizontalDividerComponentItem.tag = hrTag
            addView(horizontalDividerComponentItem, insertIndex)
            reComputeTagsAfter(insertIndex)

            //add another text component below image
            insertIndex++
            setCurrentInputMode(TextModeType.MODE_PLAIN)

            insertNewTextComponentAfterThis?.let {
                //add another text component below image
                if (it) {
                    addTextComponent(insertIndex)
                } else {
                    setFocus(horizontalDividerComponentItem)
                }
            } ?: addTextComponent(insertIndex)
            refreshViewOrder()
        }
    }

    /**
     * @return index next to focused view.
     */
    private val nextIndex: Int
        get() {
            val tag = _activeView?.tag as ComponentTag
            return ++tag.componentIndex
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
        fun onFocusedViewHas(@TextModeType mode: Int, @TextComponentStyle textComponentStyle: Int)
    }

    companion object {
        var serverUrl: String? = null
    }

    init {
        init(context)
    }
}