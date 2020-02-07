package org.smartregister.child.sample.model;

import org.smartregister.child.cursor.AdvancedMatrixCursor;
import org.smartregister.child.model.BaseChildRegisterFragmentModel;
import org.smartregister.domain.Response;

/**
 * Created by ndegwamartin on 2019-05-27.
 */
public class ChildRegisterFragmentModel extends BaseChildRegisterFragmentModel {

    @Override
    public AdvancedMatrixCursor createMatrixCursor(Response<String> response) {
        //Just overriddenn
        return null;
    }

}
