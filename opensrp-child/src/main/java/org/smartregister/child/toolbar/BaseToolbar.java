package org.smartregister.child.toolbar;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MenuItem;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;

/**
 * Created by Jason Rogena - jrogena@ona.io on 17/02/2017.
 */

public abstract class BaseToolbar extends Toolbar {
    public BaseToolbar(Context context) {
        super(context);
    }

    public BaseToolbar(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public BaseToolbar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public abstract int getSupportedMenu();

    public abstract void prepareMenu();

    public abstract MenuItem onMenuItemSelected(MenuItem menuItem);
}
