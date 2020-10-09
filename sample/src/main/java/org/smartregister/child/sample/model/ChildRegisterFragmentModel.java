package org.smartregister.child.sample.model;

import org.smartregister.child.cursor.AdvancedMatrixCursor;
import org.smartregister.child.model.BaseChildRegisterFragmentModel;
import org.smartregister.domain.Response;
import org.smartregister.view.contract.IField;

/**
 * Created by ndegwamartin on 2019-05-27.
 */
public class ChildRegisterFragmentModel extends BaseChildRegisterFragmentModel {

    @Override
    public AdvancedMatrixCursor createMatrixCursor(Response<String> response) {
        //Just overriddenn
        return null;
    }

    @Override
    public String countSelect(String s, String s1) {
        return null;
    }

    @Override
    public String mainSelect(String s, String s1) {
        return null;
    }

    @Override
    public String getSortText(IField iField) {
        return null;
    }
}
