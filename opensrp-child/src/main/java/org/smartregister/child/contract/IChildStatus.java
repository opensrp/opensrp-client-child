package org.smartregister.child.contract;

import org.smartregister.Context;
import org.smartregister.commonregistry.CommonPersonObjectClient;

public interface IChildStatus {

    IChildStatusView getView();

    void activateChildStatus(Context openSRPcontext, CommonPersonObjectClient childDetails);
}
