package com.seanlab.qrcode.mlkit.ghost.event;

import com.seanlab.qrcode.mlkit.ghost.model.entity.Post;

public class PostSyncedEvent {

    public final Post post;

    public PostSyncedEvent(Post post) {
        this.post = post;
    }

}
