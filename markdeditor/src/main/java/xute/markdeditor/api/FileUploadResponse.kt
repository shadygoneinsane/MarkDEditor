package xute.markdeditor.api

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

internal class FileUploadResponse(@field:SerializedName("error") @field:Expose var error: String, @field:SerializedName("url") @field:Expose var url: String)