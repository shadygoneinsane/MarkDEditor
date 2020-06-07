package xute.markdeditor.utilities

import android.content.Context
import android.graphics.drawable.Drawable
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target

object ImageHelper {
    fun ImageView.load(_uri: String) {
        try {
            val options = RequestOptions()
                    .fitCenter()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .priority(Priority.HIGH)
            Glide.with(context)
                    .load(_uri)
                    .apply(options)
                    .listener(object : RequestListener<Drawable> {
                        override fun onLoadFailed(e: GlideException?,
                                                  model: Any,
                                                  target: Target<Drawable>,
                                                  isFirstResource: Boolean): Boolean {
                            return false
                        }

                        override fun onResourceReady(resource: Drawable,
                                                     model: Any,
                                                     target: Target<Drawable>,
                                                     dataSource: DataSource,
                                                     isFirstResource: Boolean): Boolean {
                            val width = measuredWidth
                            val targetHeight = width * resource.intrinsicHeight / resource.intrinsicWidth
                            if (layoutParams.height != targetHeight) {
                                layoutParams.height = targetHeight
                                requestLayout()
                            }
                            setImageDrawable(resource)
                            return true
                        }
                    }).into(this)
        } catch (e: Exception) {
        }
    }
}