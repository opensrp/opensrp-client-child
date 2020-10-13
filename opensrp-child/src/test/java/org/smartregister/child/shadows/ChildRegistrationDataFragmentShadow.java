package org.smartregister.child.shadows;

import org.smartregister.child.fragment.BaseChildRegistrationDataFragment;

public class ChildRegistrationDataFragmentShadow extends BaseChildRegistrationDataFragment {

    @Override
    public String getRegistrationForm() {
        return "child_enrollment";
    }
}
