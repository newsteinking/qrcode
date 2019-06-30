package com.seanlab.qrcode.mlkit.ghost.event;

import com.seanlab.qrcode.mlkit.ghost.network.ApiFailure;

@SuppressWarnings({"WeakerAccess", "unused"})
public class FileUploadErrorEvent {

    public final ApiFailure apiFailure;

    public FileUploadErrorEvent(ApiFailure apiFailure) {
        this.apiFailure = apiFailure;
    }

}
