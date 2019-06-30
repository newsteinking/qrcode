package com.seanlab.qrcode.mlkit.ghost.event;

import androidx.annotation.Nullable;

public class LoginErrorEvent {

    public final String blogUrl;

    public LoginErrorEvent(@Nullable String blogUrl) {
        this.blogUrl = blogUrl;
    }

}
