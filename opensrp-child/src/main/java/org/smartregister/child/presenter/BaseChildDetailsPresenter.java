package org.smartregister.child.presenter;

import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.AllConstants;
import org.smartregister.child.ChildLibrary;
import org.smartregister.child.contract.ChildTabbedDetailsContract;
import org.smartregister.repository.EventClientRepository;
import org.smartregister.clientandeventmodel.DateUtil;

import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import timber.log.Timber;

public class BaseChildDetailsPresenter implements ChildTabbedDetailsContract.Presenter {

    public static final String CARD_STATUS = "card_status";
    public static final String CARD_STATUS_DATE = "card_status_date";

    public enum CardStatus {
        needs_card, does_not_need_card
    }

    private final WeakReference<ChildTabbedDetailsContract.View> view;
    private final EventClientRepository eventClientRepository = ChildLibrary.getInstance().eventClientRepository();

    public BaseChildDetailsPresenter(ChildTabbedDetailsContract.View view) {
        this.view = new WeakReference<>(view);
    }

    @Override
    public ChildTabbedDetailsContract.View getView() {
        return view.get();
    }

    @Override
    public void reportLostCard(String baseEntityId) {
        if (getView() != null) {
            JSONObject client = eventClientRepository.getClientByBaseEntityId(baseEntityId);
            try {
                JSONObject clientAttributes = client.getJSONObject(AllConstants.ATTRIBUTES);
                clientAttributes.put(CARD_STATUS, CardStatus.needs_card.name());
                Date currentTime = Calendar.getInstance().getTime();
                clientAttributes.put(CARD_STATUS_DATE, DateUtil.fromDate(currentTime));
                client.put(AllConstants.ATTRIBUTES, clientAttributes);
                eventClientRepository.addorUpdateClient(baseEntityId, client);
                getView().notifyLostCardReported(new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH).format(currentTime));
            } catch (JSONException e) {
                Timber.e(e);
            }
        }
    }
}
