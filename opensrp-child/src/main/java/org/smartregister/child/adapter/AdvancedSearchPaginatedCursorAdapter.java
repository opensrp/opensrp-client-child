package org.smartregister.child.adapter;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import org.smartregister.child.R;
import org.smartregister.child.provider.AdvancedSearchClientsProvider;
import org.smartregister.commonregistry.CommonRepository;
import org.smartregister.cursoradapter.RecyclerViewCursorAdapter;

/**
 * Created by keyman on 4/5/17.
 */
public class AdvancedSearchPaginatedCursorAdapter extends RecyclerViewCursorAdapter {
    private final AdvancedSearchClientsProvider listItemProvider;
    private final CommonRepository commonRepository;
    private final Context context;

    public AdvancedSearchPaginatedCursorAdapter(Context context, Cursor c, AdvancedSearchClientsProvider listItemProvider,
                                                CommonRepository commonRepository) {
        super(c);
        this.listItemProvider = listItemProvider;
        this.commonRepository = commonRepository;
        this.context = context;
    }

   /* @Override
    protected Object getSectionFromCursor(Cursor cursor) {

        if (cursor != null && cursor.getCount() > 0) {
            String inactive = "";
            int index = cursor.getColumnIndex("inactive");
            if (index != -1) {
                inactive = cursor.getString(index);
            }

            if (StringUtils.isNotBlank(inactive) && inactive.equals(Boolean.TRUE.toString())) {
                return "INACTIVE OR LOST TO FOLLOW-UP";
            }

            String lostToFollowUp = "";
            index = cursor.getColumnIndex("lost_to_follow_up");
            if (index != -1) {
                lostToFollowUp = cursor.getString(index);
            }

            if (StringUtils.isNotBlank(lostToFollowUp) && lostToFollowUp.equals(Boolean.TRUE.toString())) {
                return "INACTIVE OR LOST TO FOLLOW-UP";
            }
        }
        return "ACTIVE";
    }*/


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, Cursor cursor) {
/*

        CommonPersonObject personinlist = commonRepository.readAllcommonforCursorAdapter(cursor);
        CommonPersonObjectClient pClient = new CommonPersonObjectClient(personinlist.getCaseId(), personinlist.getDetails
        (), personinlist.getDetails().get("FWHOHFNAME"));
        pClient.setColumnmaps(personinlist.getColumnmaps());
        listItemProvider.getView(cursor, pClient, ((ChildRegisterProvider.RegisterViewHolder)viewHolder));*/

    }

    @Override
    public Cursor swapCursor(Cursor newCursor) {
        return super.swapCursor(newCursor);
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = ((Activity) context).getLayoutInflater().inflate(R.layout.advanced_search_section, viewGroup, false);
        return new ViewHolder(view);
    }


    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder {
        public View parent;

        ViewHolder(View itemView) {
            super(itemView);
            parent = itemView;
        }
    }
}
