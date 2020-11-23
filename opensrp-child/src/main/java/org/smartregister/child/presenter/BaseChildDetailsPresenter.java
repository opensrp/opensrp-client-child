package org.smartregister.child.presenter;

import org.smartregister.child.contract.ChildTabbedDetailsContract;

import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class BaseChildDetailsPresenter implements ChildTabbedDetailsContract.Presenter {

    private final WeakReference<ChildTabbedDetailsContract.View> view;

    public BaseChildDetailsPresenter(ChildTabbedDetailsContract.View view) {
        this.view = new WeakReference<>(view);
    }

    @Override
    public ChildTabbedDetailsContract.View getView() {
        return view.get();
    }

    @Override
    public void reportLostCard(String baseEntityId) {
        if (getView() != null) {
            String orderDate = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH).format(new Date());
            getView().notifyLostCardReported(orderDate);
        }
    }
}
