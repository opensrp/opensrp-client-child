package org.smartregister.child.view;

import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

import org.smartregister.child.activity.BaseActivity;
import org.smartregister.child.task.GetChildDetailsTask;

/**
 * Created by ndegwamartin on 06/03/2019.
 */
public class SiblingPicture extends RecyclerView.ViewHolder {

    private final View itemView;

    public SiblingPicture(View itemView) {
        super(itemView);
        this.itemView = itemView;
    }

    public void setChildBaseEntityId(BaseActivity baseActivity, String baseEntityId) {
        (new GetChildDetailsTask(baseActivity, baseEntityId, itemView)).execute();
    }
}
