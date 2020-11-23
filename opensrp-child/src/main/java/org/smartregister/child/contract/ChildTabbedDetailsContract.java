package org.smartregister.child.contract;

public interface ChildTabbedDetailsContract {

    interface View {
        void notifyLostCardReported(String orderDate);
    }

    interface Presenter {
        View getView();

        void reportLostCard(String baseEntityId);
    }
}
