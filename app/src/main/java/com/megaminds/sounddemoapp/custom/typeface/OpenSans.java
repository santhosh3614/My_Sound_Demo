package com.megaminds.sounddemoapp.custom.typeface;

import android.content.Context;
import android.graphics.Typeface;

public class OpenSans {

    private static OpenSans instance;
    private Typeface typeface;

    private OpenSans(Context context) {
        typeface = Typeface.createFromAsset(context.getApplicationContext().getResources().getAssets(),
                "open_sans_light.ttf");
    }

    public static OpenSans getInstance(Context context) {
        synchronized (OpenSans.class) {
            if (instance == null)
                instance = new OpenSans(context);
            return instance;
        }
    }

    public Typeface getTypeFace() {
        return typeface;
    }
}
