package com.seanlab.qrcode.mlkit.ghost.event;

import com.seanlab.qrcode.mlkit.ghost.model.entity.User;

public class UserLoadedEvent {

    public final User user;

    public UserLoadedEvent(User user) {
        this.user = user;
    }

}
