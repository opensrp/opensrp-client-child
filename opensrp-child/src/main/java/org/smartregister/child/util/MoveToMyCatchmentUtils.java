package org.smartregister.child.util;

import android.app.ProgressDialog;
import android.os.AsyncTask;

import androidx.core.util.Pair;

import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nullable;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.Context;
import org.smartregister.CoreLibrary;
import org.smartregister.DristhiConfiguration;
import org.smartregister.child.domain.MoveToCatchmentEvent;
import org.smartregister.clientandeventmodel.Event;
import org.smartregister.domain.Response;
import org.smartregister.domain.ResponseStatus;
import org.smartregister.event.Listener;
import org.smartregister.sync.helper.ECSyncHelper;
import org.smartregister.sync.intent.SyncIntentService;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

/**
 * Created by keyman on 26/01/2017.
 */
public class MoveToMyCatchmentUtils {
    public static final String MOVE_TO_CATCHMENT_EVENT = "Move To Catchment";
    public static final String MOVE_TO_CATCHMENT_SYNC_EVENT = "MOVE_TO_CATCHMENT_SYNC";
    public static final String MOVE_TO_CATCHMENT_IDENTIFIERS_FORM_FIELD = "Identifiers";

    public static void moveToMyCatchment(final List<String> ids, final Listener<MoveToCatchmentEvent> listener, final ProgressDialog progressDialog, final boolean isPermanent) {

        org.smartregister.util.Utils.startAsyncTask(new AsyncTask<Void, Void, MoveToCatchmentEvent>() {

            @Override
            protected void onPreExecute() {
                progressDialog.show();
            }

            @Override
            protected MoveToCatchmentEvent doInBackground(Void... params) {
                publishProgress();
                return createMoveToCatchmentEvent(ids, isPermanent, true);
            }

            @Override
            protected void onPostExecute(MoveToCatchmentEvent result) {
                listener.onEvent(result);
                progressDialog.dismiss();
            }
        }, null);
    }

    @Nullable
    public static MoveToCatchmentEvent createMoveToCatchmentEvent(List<String> ids, boolean isPermanent, boolean shouldCreateEvent) {
        Response<String> response = httpRequest(ids);
        if (response.isFailure()) {
            return null;
        } else {
            try {
                return new MoveToCatchmentEvent(new JSONObject(response.payload()), isPermanent, shouldCreateEvent);
            } catch (Exception e) {
                Timber.e(e);
                return null;
            }
        }
    }

    private static Response<String> httpRequest(List<String> baseEntityIds) {
        if (baseEntityIds == null || baseEntityIds.isEmpty()) {
            return new Response<>(ResponseStatus.failure, "entityId doesn't exist");
        }

        Context context = CoreLibrary.getInstance().context();
        DristhiConfiguration configuration = context.configuration();

        String baseUrl = configuration.dristhiBaseURL();
        String idString = StringUtils.join(baseEntityIds, ",");

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

    public static List<Pair<Event, JSONObject>> createEventList(ECSyncHelper ecSyncHelper, JSONArray events) throws JSONException {
        List<Pair<Event, JSONObject>> eventList = new ArrayList<>();

        for (int i = 0; i < events.length(); i++) {
            JSONObject jsonEvent = events.getJSONObject(i);
            Event event = ecSyncHelper.convert(jsonEvent, Event.class);
            if (event == null) {
                continue;
            }

            // Skip previous move to catchment events
            if (MoveToMyCatchmentUtils.MOVE_TO_CATCHMENT_EVENT.equals(event.getEventType()) || MoveToMyCatchmentUtils.MOVE_TO_CATCHMENT_SYNC_EVENT.equals(event.getEventType())) {
                continue;
            }

            if (Constants.EventType.BITRH_REGISTRATION.equals(event.getEventType())) {
                eventList.add(0, Pair.create(event, jsonEvent));
            } else if (!eventList.isEmpty() && Constants.EventType.NEW_WOMAN_REGISTRATION.equals(event.getEventType())) {
                eventList.add(1, Pair.create(event, jsonEvent));
            } else {
                eventList.add(Pair.create(event, jsonEvent));
            }
        }

        return eventList;
    }

}
