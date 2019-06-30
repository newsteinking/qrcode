package com.seanlab.qrcode.mlkit.ghost.network.entity;

import androidx.annotation.NonNull;

import com.seanlab.qrcode.mlkit.ghost.model.entity.Tag;

@SuppressWarnings({"WeakerAccess", "unused"})
public class TagStub {

    public final String name;

    public TagStub(@NonNull Tag tag) {
        this.name = tag.getName();
    }

}
