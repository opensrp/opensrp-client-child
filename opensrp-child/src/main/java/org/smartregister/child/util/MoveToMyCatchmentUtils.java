package org.smartregister.child.util;

import android.app.ProgressDialog;
import android.os.AsyncTask;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;
import org.smartregister.Context;
import org.smartregister.CoreLibrary;
import org.smartregister.DristhiConfiguration;
import org.smartregister.domain.Response;
import org.smartregister.domain.ResponseStatus;
import org.smartregister.event.Listener;
import org.smartregister.repository.AllSharedPreferences;
import org.smartregister.sync.intent.SyncIntentService;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

import timber.log.Timber;

/**
 * Created by keyman on 26/01/2017.
 */
public class MoveToMyCatchmentUtils {
    public static final String MOVE_TO_CATCHMENT_EVENT = "Move To Catchment";

    public static void moveToMyCatchment(final List<String> ids, final Listener<JSONObject> listener,
                                         final ProgressDialog progressDialog) {

        org.smartregister.util.Utils.startAsyncTask(new AsyncTask<Void, Void, JSONObject>() {

            @Override
            protected void onPreExecute() {
                progressDialog.show();
            }

            @Override
            protected JSONObject doInBackground(Void... params) {
                publishProgress();
                Response<String> response = move(ids);
                if (response.isFailure()) {
                    return null;
                } else {
                    try {
                        return new JSONObject(response.payload());
                    } catch (Exception e) {
                        Timber.e(e);
                        return null;
                    }
                }
            }

            @Override
            protected void onPostExecute(JSONObject result) {
                listener.onEvent(result);
                progressDialog.dismiss();
            }
        }, null);
    }

    private static Response<String> move(List<String> ids) {
        if (ids == null || ids.isEmpty()) {
            return new Response<>(ResponseStatus.failure, "entityId doesn't exist");
        }

        Context context = CoreLibrary.getInstance().context();
        DristhiConfiguration configuration = context.configuration();

        String baseUrl = configuration.dristhiBaseURL();
        String idString = StringUtils.join(ids, ",");

        String paramString = "?baseEntityId=" + urlEncode(idString.trim()) + "&limit=1000";
        String uri = baseUrl + SyncIntentService.SYNC_URL + paramString;

        Timber.d(MoveToMyCatchmentUtils.class.getCanonicalName() + " " + uri);

        return context.getHttpAgent().fetch(uri);
    }

    private static String urlEncode(String value) {
        try {
            return URLEncoder.encode(value, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            return value;
        }
    }

    public static boolean processMoveToCatchment(android.content.Context context, AllSharedPreferences allSharedPreferences,
                                                 JSONObject jsonObject) {
        return ChildJsonFormUtils.processMoveToCatchment(context, allSharedPreferences, jsonObject);
    }

}
