package com.megaminds.sounddemoapp.custom.typeface;

import android.content.Context;
import android.graphics.Typeface;

public class OpenSans {

    private static OpenSans instance;
    private Typeface typeface;

    public OpenSans() {
    }

    public static OpenSans getInstance() {
        synchronized (OpenSans.class) {
            if (instance == null)
                instance = new OpenSans();
            return instance;
        }
    }

    public Typeface getTypeFace(Context context) {
        if (typeface == null) {
            typeface = Typeface.createFromAsset(context.getApplicationContext().getResources().getAssets(),
                    "open_sans_light.ttf");
        }
        return typeface;
    }
}
