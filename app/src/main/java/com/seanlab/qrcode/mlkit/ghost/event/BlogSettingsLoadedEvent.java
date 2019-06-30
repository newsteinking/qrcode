package com.seanlab.qrcode.mlkit.ghost.event;

import java.util.List;

import com.seanlab.qrcode.mlkit.ghost.model.entity.Setting;

public class BlogSettingsLoadedEvent {

    public final List<Setting> settings;

    public BlogSettingsLoadedEvent(List<Setting> settings) {
        this.settings = settings;
    }

}
