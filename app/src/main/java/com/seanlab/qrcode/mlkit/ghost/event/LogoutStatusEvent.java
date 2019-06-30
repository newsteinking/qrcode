package com.seanlab.qrcode.mlkit.ghost.event;

public class LogoutStatusEvent {

    public final boolean succeeded;
    public final boolean hasPendingActions;

    public LogoutStatusEvent(boolean succeeded, boolean hasPendingActions) {
        this.succeeded = succeeded;
        this.hasPendingActions = hasPendingActions;
    }

}
