package com.seanlab.qrcode.mlkit.md.common;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;

/** Draw camera image to background. */
public class CameraImageGraphic extends GraphicOverlayLabel.Graphic {

    private final Bitmap bitmap;

    public CameraImageGraphic(GraphicOverlayLabel overlay, Bitmap bitmap) {
        super(overlay);
        this.bitmap = bitmap;
    }

    @Override
    public void draw(Canvas canvas) {
        canvas.drawBitmap(bitmap, null, new Rect(0, 0, canvas.getWidth(), canvas.getHeight()), null);
    }
}

