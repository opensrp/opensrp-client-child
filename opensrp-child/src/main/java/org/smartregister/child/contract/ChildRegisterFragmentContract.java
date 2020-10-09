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

        void recalculatePagination(AdvancedMatrixCursor matrixCursor);

        ChildRegisterFragmentContract.Presenter presenter();

    }

    interface Presenter extends BaseRegisterFragmentContract.Presenter {

        String getMainCondition();

        String getDefaultSortQuery();

    }

    interface Model extends BaseRegisterFragmentContract.Model{

        RegisterConfiguration defaultRegisterConfiguration();

        String countSelect(String mainCondition);

        String mainSelect(String mainCondition);

        String getSortText(Field sortField);

        AdvancedMatrixCursor createMatrixCursor(Response<String> response);

        JSONArray getJsonArray(Response<String> response);
    }


}
