package com.z.stproperty.fonts;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

public class Helvetica extends TextView{

	public Helvetica(Context context) {
        super(context);
        initText();
    }

    public Helvetica(Context context, AttributeSet attrs) {
        super(context, attrs);
        initText();
    }

    public Helvetica(Context context, AttributeSet attrs, int defStyle) {
    	super(context, attrs, defStyle);
    	initText();
    }

    public final void initText() {
        Typeface tf = Typefaces.get(getContext(), "fonts/helvetica.ttf");
        setTypeface(tf);
    }
}
