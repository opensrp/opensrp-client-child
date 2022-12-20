package org.smartregister.child.contract;

public interface ChildTabbedDetailsContract {

    interface View extends IChildStatusView {

        void notifyLostCardReported(String cardStatusDate);
    }

    interface Presenter extends IChildStatus {

        void reportLostCard(String baseEntityId);
    }
}
