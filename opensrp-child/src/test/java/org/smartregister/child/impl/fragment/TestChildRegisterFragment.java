package org.smartregister.child.impl.fragment;

import android.view.View;

import org.smartregister.child.fragment.BaseChildRegisterFragment;

public class TestChildRegisterFragment extends BaseChildRegisterFragment {
    @Override
    protected String getMainCondition() {
        return "is_closed is 0";
    }

    @Override
    protected String getDefaultSortQuery() {
        return presenter().getDefaultSortQuery();
    }

    @Override
    protected String filterSelectionCondition(boolean urgentOnly) {
        return  "in_active is 1";
    }

    @Override
    public void onClick(View v) {
        //Do nothing
    }
}
