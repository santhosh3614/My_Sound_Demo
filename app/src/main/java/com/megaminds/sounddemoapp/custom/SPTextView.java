package com.megaminds.sounddemoapp.custom;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

import com.megaminds.sounddemoapp.custom.typeface.OpenSans;


public class SPTextView extends TextView {

	public SPTextView(Context context) {
		super(context);
		setTypeface(OpenSans.getInstance().getTypeFace(context));
	}

	public SPTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		setTypeface(OpenSans.getInstance().getTypeFace(context));
	}

	public SPTextView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		setTypeface(OpenSans.getInstance().getTypeFace(context));
	}

}
