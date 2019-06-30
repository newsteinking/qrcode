package com.seanlab.qrcode.mlkit.ghost.event;

import com.seanlab.qrcode.mlkit.ghost.model.entity.Post;

public class PostCreatedEvent {

    public final Post newPost;

    public PostCreatedEvent(Post newPost) {
        this.newPost = newPost;
    }

}
