package xute.markdeditor.components

import android.content.Context
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.widget.*
import xute.markdeditor.R
import xute.markdeditor.api.ImageUploader
import xute.markdeditor.api.ImageUploader.ImageUploadCallback
import xute.markdeditor.models.ComponentTag
import xute.markdeditor.models.ImageComponentModel
import xute.markdeditor.utilities.ImageHelper.load

class ImageComponentItem : FrameLayout, ImageUploadCallback {
    var imageView: ImageView? = null
    var captionEt: EditText? = null
    var imageUploadProgressBar: ProgressBar? = null
    var retryUpload: ImageView? = null
    var statusMessage: TextView? = null
    var removeImageButton: ImageView? = null
    var isImageUploaded = false
    var isImageUploading = false
    private var downloadUrl: String? = null

    var mCaption: String? = null
    fun setCaption(caption: String) {
        val tag = tag as ComponentTag
        (tag.component as ImageComponentModel).caption = caption
    }

    private var filePath: String? = null
    private var imageUploader: ImageUploader? = null
    private var mContext: Context? = null
    private var imageComponentListener: ImageComponentListener? = null
    private val imageClickListener: OnClickListener = OnClickListener {
        when {
            isImageUploaded -> uploadedState()
            isImageUploading -> uploadingState()
            else -> failedState()
        }
        hideExtraInfroWithDelay()
    }
    private var serverToken: String? = null

    constructor(context: Context) : super(context) {
        init(context)
    }

    private fun init(context: Context) {
        mContext = context
        downloadUrl = null
        imageUploader = ImageUploader()
        imageUploader?.setImageUploadCallback(this)
        val view = LayoutInflater.from(context).inflate(R.layout.image_component_item, this)
        imageView = view.findViewById(R.id.image)
        captionEt = view.findViewById(R.id.caption)
        retryUpload = view.findViewById(R.id.retry_image_upload_btn)
        imageUploadProgressBar = view.findViewById(R.id.image_uploading_progress_bar)
        statusMessage = view.findViewById(R.id.message)
        removeImageButton = view.findViewById(R.id.removeImageBtn)
        attachListeners()
    }

    private fun attachListeners() {
        retryUpload?.setOnClickListener {
            filePath?.let { setImageInformation(it, serverToken, false, "") }
        }
        removeImageButton?.setOnClickListener {
            if (imageComponentListener != null) {
                imageComponentListener?.onImageRemove(selfIndex())
            }
        }
        captionEt?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                checkReturnPressToInsertNewTextComponent(charSequence)
                mCaption = charSequence.toString()
            }

            override fun afterTextChanged(editable: Editable) {}
        })
    }

    fun setImageInformation(filePath: String, serverToken: String?, imageUploaded: Boolean, caption: String?) {
        this.serverToken = serverToken
        this.filePath = filePath
        this.mCaption = caption
        if (caption != null) {
            captionEt?.setText(caption)
        }
        loadImage(filePath)
        if (imageUploaded) {
            onImageUploaded(filePath)
        } else {
            startImageUpload(filePath)
        }
    }

    private fun selfIndex(): Int {
        val tag = tag as ComponentTag
        return tag.componentIndex
    }

    private fun checkReturnPressToInsertNewTextComponent(charSequence: CharSequence) {
        val clen = charSequence.length
        if (clen > 0) {
            val sequenceToCheckNewLineCharacter = if (clen > 1) charSequence.subSequence(clen - 2, clen).toString() else charSequence.subSequence(clen - 1, clen).toString()
            val noReadableCharactersAfterCursor = sequenceToCheckNewLineCharacter.trim { it <= ' ' }.isEmpty()
            //if last characters are [AB\n<space>] or [AB\n] then we insert new TextComponent
            //else if last characters are [AB\nC] ignore the insert.
            if (sequenceToCheckNewLineCharacter.contains("\n") && noReadableCharactersAfterCursor) {
                if (imageComponentListener != null) {
                    imageComponentListener?.onExitFromCaptionAndInsertNewTextComponent(selfIndex() + 1)
                }
            }
        }
    }

    private fun loadImage(filePath: String) {
        imageView?.load(filePath)
    }

    override fun onImageUploaded(downloadUrl: String) {
        setDownloadUrl(downloadUrl)
        isImageUploading = false
        isImageUploaded = true
        uploadedState()
    }

    /**
     * Uploads image to server.
     *
     * @param filePath local path of image to be uploaded.
     */
    private fun startImageUpload(filePath: String?) {
        isImageUploading = true
        uploadingState()
        imageUploader?.uploadImage(filePath, serverToken)
    }

    private fun uploadedState() {
        removeImageButton?.visibility = View.VISIBLE
        retryUpload?.visibility = View.GONE
        imageUploadProgressBar?.visibility = View.GONE
        statusMessage?.visibility = View.VISIBLE
        statusMessage?.text = "\u2713 Uploaded"
        hideExtraInfroWithDelay()
        //set listener
        imageView?.setOnClickListener(imageClickListener)
    }

    private fun uploadingState() {
        retryUpload?.visibility = View.GONE
        statusMessage?.visibility = View.VISIBLE
        statusMessage?.text = "Uploading..."
        imageUploadProgressBar?.visibility = View.VISIBLE
        removeImageButton?.visibility = View.VISIBLE
        //remove listener
        imageView?.setOnClickListener(null)
    }

    private fun hideExtraInfroWithDelay() {
        Handler().postDelayed({
            statusMessage?.visibility = View.GONE
            removeImageButton?.visibility = View.GONE
            retryUpload?.visibility = View.GONE
            imageUploadProgressBar?.visibility = View.GONE
        }, 2000)
    }

    override fun onImageUploadFailed() {
        setDownloadUrl(null)
        isImageUploading = false
        isImageUploaded = false
        failedState()
    }

    private fun failedState() {
        retryUpload?.visibility = View.VISIBLE
        statusMessage?.visibility = View.VISIBLE
        statusMessage?.text = "Failed To Upload. Try again!"
        imageUploadProgressBar?.visibility = View.GONE
        removeImageButton?.visibility = View.VISIBLE
        //remove listener
        imageView?.setOnClickListener(null)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init(context)
    }

    fun getDownloadUrl(): String? {
        return downloadUrl
    }

    private fun setDownloadUrl(downloadUrl: String?) {
        this.downloadUrl = downloadUrl
        val tag = tag as ComponentTag
        (tag.component as ImageComponentModel).url = downloadUrl
    }

    fun setFocus() {
        imageView?.isEnabled = true
        captionEt?.requestFocus()
    }

    fun setImageComponentListener(imageComponentListener: ImageComponentListener?) {
        this.imageComponentListener = imageComponentListener
    }

    interface ImageComponentListener {
        fun onImageRemove(removeIndex: Int)
        fun onExitFromCaptionAndInsertNewTextComponent(currentIndex: Int)
    }
}