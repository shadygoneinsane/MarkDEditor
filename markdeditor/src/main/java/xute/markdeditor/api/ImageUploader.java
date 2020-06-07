package xute.markdeditor.api;

import org.jetbrains.annotations.NotNull;

import java.io.File;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ImageUploader {

    private ImageUploadCallback imageUploadCallback;

    public void uploadImage(String filePath, String serverToken) {
        File file = new File(filePath);
        final RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), file);
        MultipartBody.Part body = MultipartBody.Part.createFormData("upload", file.getName(), requestFile);
        RetrofitApiClient.getClient(serverToken)
                .create(Api.class)
                .uploadFile(body)
                .enqueue(new Callback<FileUploadResponse>() {
                    @Override
                    public void onResponse(@NotNull Call<FileUploadResponse> call, @NotNull Response<FileUploadResponse> response) {
                        if (response.isSuccessful()) {
                            if (imageUploadCallback != null) {
                                imageUploadCallback.onImageUploaded(response.body().getUrl());
                            }
                        } else {
                            if (imageUploadCallback != null) {
                                imageUploadCallback.onImageUploadFailed();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<FileUploadResponse> call, Throwable t) {
                        if (imageUploadCallback != null) {
                            imageUploadCallback.onImageUploadFailed();
                        }
                    }
                });
    }

    public void setImageUploadCallback(ImageUploadCallback imageUploadCallback) {
        this.imageUploadCallback = imageUploadCallback;
    }

    public interface ImageUploadCallback {
        void onImageUploaded(String downloadUrl);

        void onImageUploadFailed();
    }
}
