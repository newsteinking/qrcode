package com.seanlab.qrcode.mlkit.ghost.event;

import com.seanlab.qrcode.mlkit.ghost.model.entity.Post;

public class PostSavedEvent {

    public final Post post;

    public PostSavedEvent(Post post) {
        this.post = post;
    }

}
