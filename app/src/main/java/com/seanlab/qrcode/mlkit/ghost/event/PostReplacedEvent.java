package com.seanlab.qrcode.mlkit.ghost.event;

import com.seanlab.qrcode.mlkit.ghost.model.entity.Post;

public class PostReplacedEvent {

    public final Post newPost;

    public PostReplacedEvent(Post newPost) {
        this.newPost = newPost;
    }

}
