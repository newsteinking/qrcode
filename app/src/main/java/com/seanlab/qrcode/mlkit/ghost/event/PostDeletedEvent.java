package com.seanlab.qrcode.mlkit.ghost.event;

public class PostDeletedEvent {

    public final String postId;

    public PostDeletedEvent(String postId) {
        this.postId = postId;
    }
}
