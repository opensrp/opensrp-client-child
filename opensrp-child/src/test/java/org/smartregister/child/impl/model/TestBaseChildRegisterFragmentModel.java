package org.smartregister.child.impl.model;

import org.smartregister.child.cursor.AdvancedMatrixCursor;
import org.smartregister.child.model.BaseChildRegisterFragmentModel;
import org.smartregister.domain.Response;

public class TestBaseChildRegisterFragmentModel extends BaseChildRegisterFragmentModel {
    @Override
    protected String[] mainColumns(String tableName, String parentTableName) {
        return new String[0];
    }

    @Override
    public AdvancedMatrixCursor createMatrixCursor(Response<String> response) {
        return null;
    }
}
