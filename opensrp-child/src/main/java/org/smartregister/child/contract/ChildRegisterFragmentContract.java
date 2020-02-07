package org.smartregister.child.contract;

import org.json.JSONArray;
import org.smartregister.child.cursor.AdvancedMatrixCursor;
import org.smartregister.configurableviews.model.Field;
import org.smartregister.configurableviews.model.RegisterConfiguration;
import org.smartregister.configurableviews.model.ViewConfiguration;
import org.smartregister.domain.Response;
import org.smartregister.view.contract.BaseRegisterFragmentContract;

import java.util.List;
import java.util.Set;

/**
 * Created by ndegwamartin on 25/02/2019.
 */
public interface ChildRegisterFragmentContract {


    interface View extends BaseRegisterFragmentContract.View {

        void initializeAdapter(Set<org.smartregister.configurableviews.model.View> visibleColumns);

        void recalculatePagination(AdvancedMatrixCursor matrixCursor);

        ChildRegisterFragmentContract.Presenter presenter();

    }

    interface Presenter extends BaseRegisterFragmentContract.Presenter {

        void updateSortAndFilter(List<Field> filterList, Field sortField);

        String getMainCondition();

        String getDefaultSortQuery();

    }

    interface Model {

        RegisterConfiguration defaultRegisterConfiguration();

        ViewConfiguration getViewConfiguration(String viewConfigurationIdentifier);

        Set<org.smartregister.configurableviews.model.View> getRegisterActiveColumns(String viewConfigurationIdentifier);

        String countSelect(String tableName, String mainCondition, String parentTableName);

        String mainSelect(String tableName, String mainCondition, String parentTableName);

        String mainSelect(String mainCondition);

        String getFilterText(List<Field> filterList, String filter);

        String getSortText(Field sortField);

        AdvancedMatrixCursor createMatrixCursor(Response<String> response);

        JSONArray getJsonArray(Response<String> response);

    }


}
