package org.smartregister.child.shadows;

import org.apache.commons.lang3.tuple.Triple;
import org.smartregister.child.activity.BaseChildDetailTabbedActivity;
import org.smartregister.child.fragment.BaseChildRegistrationDataFragment;

import java.util.Map;

public class ChildDetailTabbedActivityShadow extends BaseChildDetailTabbedActivity {

    @Override
    protected BaseChildRegistrationDataFragment getChildRegistrationDataFragment() {
        return new ChildRegistrationDataFragmentShadow();
    }

    @Override
    protected void navigateToRegisterActivity() {

    }

    @Override
    public void onUniqueIdFetched(Triple<String, Map<String, String>, String> triple, String entityId) {

    }

    @Override
    public void onNoUniqueId() {

    }
}
