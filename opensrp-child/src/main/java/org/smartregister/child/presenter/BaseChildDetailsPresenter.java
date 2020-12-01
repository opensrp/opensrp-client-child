package org.smartregister.child.presenter;

import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.AllConstants;
import org.smartregister.child.ChildLibrary;
import org.smartregister.child.contract.ChildTabbedDetailsContract;
import org.smartregister.clientandeventmodel.DateUtil;
import org.smartregister.repository.EventClientRepository;

import java.lang.ref.WeakReference;
import java.util.Calendar;

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
                String cardStatusDate = DateUtil.fromDate( Calendar.getInstance().getTime());
                clientAttributes.put(CARD_STATUS, CardStatus.needs_card.name());
                clientAttributes.put(CARD_STATUS_DATE, cardStatusDate);
                client.put(AllConstants.ATTRIBUTES, clientAttributes);
                eventClientRepository.addorUpdateClient(baseEntityId, client);
                getView().notifyLostCardReported(cardStatusDate);
            } catch (JSONException e) {
                Timber.e(e);
            }
        }
    }
}
