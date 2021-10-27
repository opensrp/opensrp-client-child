package org.smartregister.child.task;

import android.content.Context;
import android.os.AsyncTask;

import org.smartregister.child.ChildLibrary;
import org.smartregister.child.R;
import org.smartregister.child.contract.IChildStatus;

/**
 * Created by ndegwamartin on 01/09/2020.
 */
public class SaveChildStatusTask extends AsyncTask<Void, Void, Void> {

    private IChildStatus presenter;
    private Context context;

    public SaveChildStatusTask(Context context, IChildStatus presenter) {
        this.presenter = presenter;
        this.context = context;
    }

    @Override
    protected void onPreExecute() {
        presenter.getView().showProgressDialog(context.getResources().getString(R.string.updating_dialog_title), "");
    }

    @Override
    protected Void doInBackground(Void... params) {
        presenter.activateChildStatus(ChildLibrary.getInstance().context(), presenter.getView().getChildDetails());

        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        presenter.getView().hideProgressDialog();
        super.onPostExecute(aVoid);
        presenter.getView().updateViews();
    }
}
