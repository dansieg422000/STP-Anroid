package com.z.stproperty.fonts;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

public class HelveticaBold extends TextView{

	public HelveticaBold(Context context) {
        super(context);
        initText();
    }

    public HelveticaBold(Context context, AttributeSet attrs) {
        super(context, attrs);
        initText();
    }

    public HelveticaBold(Context context, AttributeSet attrs, int defStyle) {
    	super(context, attrs, defStyle);
    	initText();
    }

    public final void initText() {
        Typeface tf = Typefaces.get(getContext(), "fonts/helvetica-bold.ttf");
        setTypeface(tf);
    }
}
