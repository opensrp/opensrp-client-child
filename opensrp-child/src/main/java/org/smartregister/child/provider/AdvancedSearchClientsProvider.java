package org.smartregister.child.provider;

import android.content.Context;
import android.database.Cursor;
import android.view.View;

import org.smartregister.child.R;
import org.smartregister.child.domain.RepositoryHolder;
import org.smartregister.service.AlertService;
import org.smartregister.view.contract.SmartRegisterClient;

import java.util.Set;

/**
 * Created by ndegwamartin on 05/03/2019.
 */
public class AdvancedSearchClientsProvider extends ChildRegisterProvider {

    public AdvancedSearchClientsProvider(Context context, RepositoryHolder repositoryHolder, Set visibleColumns,
                                         View.OnClickListener onClickListener, View.OnClickListener paginationClickListener,
                                         AlertService alertService) {
        super(context, repositoryHolder, visibleColumns, onClickListener, paginationClickListener, alertService);

    }

    public void getView(Cursor cursor, SmartRegisterClient client, RegisterViewHolder convertView) {
        super.getView(cursor, client, convertView);

    }

    public View inflatelayoutForCursorAdapter() {
        return inflater().inflate(R.layout.advanced_search_client, null);
    }
}
