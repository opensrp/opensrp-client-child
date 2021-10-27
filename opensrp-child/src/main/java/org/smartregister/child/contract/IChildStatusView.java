package org.smartregister.child.contract;

import org.smartregister.commonregistry.CommonPersonObjectClient;

public interface IChildStatusView {

    void updateViews();

    void showProgressDialog();

    void showProgressDialog(String title, String message);

    void hideProgressDialog();

    CommonPersonObjectClient getChildDetails();
}
