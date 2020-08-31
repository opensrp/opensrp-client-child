package org.smartregister.child.contract;

import android.database.Cursor;

import org.smartregister.child.model.ChildMotherDetailModel;
import org.smartregister.domain.Response;

import java.util.List;
import java.util.Map;

public interface ChildAdvancedSearchContract {

    interface Presenter extends ChildRegisterFragmentContract.Presenter {
        void search(Map<String, String> searchMap, boolean isLocal);
    }

    interface View extends ChildRegisterFragmentContract.View {
        void switchViews(boolean showList);

        void updateSearchCriteria(String searchCriteriaString);

        String filterAndSortQuery();

        Cursor getRawCustomQueryForAdapter(String query);

    }

    interface Model extends ChildRegisterFragmentContract.Model {

        Map<String, String> createEditMap(Map<String, String> searchMap);

        String createSearchString(Map<String, String> searchMap);

        String getMainConditionString(Map<String, String> editMap);

        List<ChildMotherDetailModel> getChildMotherDetailModels(Response<String> response);

        String[] getColumns();

    }


    interface Interactor {
        void search(Map<String, String> editMap, InteractorCallBack callBack, String opensrpID);
    }

    interface InteractorCallBack {
        void onResultsFound(Response<String> response, String opensrpID);
    }
}
