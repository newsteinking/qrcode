package com.seanlab.qrcode.mlkit.ghost.event;

import com.seanlab.qrcode.mlkit.ghost.model.entity.Post;

public class DeletePostEvent {

    public final Post post;

    public DeletePostEvent(Post post) {
        this.post = post;
    }

}
