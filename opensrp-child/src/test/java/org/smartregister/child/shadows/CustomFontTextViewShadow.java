package org.smartregister.child.shadows;

import android.content.Context;
import android.util.AttributeSet;

import org.robolectric.annotation.Implements;
import org.robolectric.shadows.ShadowTextView;
import org.smartregister.view.customcontrols.CustomFontTextView;
import org.smartregister.view.customcontrols.FontVariant;

@Implements(CustomFontTextView.class)
public class CustomFontTextViewShadow extends ShadowTextView {


    public void __constructor__(Context context, AttributeSet attrs, int defStyle) {
        //do nothing
    }

    public void setFontVariant(final FontVariant variant) {
        //do nothing
    }
}
