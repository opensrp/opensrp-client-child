package org.smartregister.child.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.vijay.jsonwizard.constants.JsonFormConstants;

import org.apache.commons.lang3.math.NumberUtils;
import org.smartregister.child.R;
import org.smartregister.child.adapter.ChildRegistrationDataAdapter;
import org.smartregister.child.domain.Field;
import org.smartregister.child.domain.Form;
import org.smartregister.child.domain.KeyValueItem;
import org.smartregister.child.util.Constants;
import org.smartregister.child.util.JsonFormUtils;
import org.smartregister.child.util.Utils;
import org.smartregister.cloudant.models.Client;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.location.helper.LocationHelper;
import org.smartregister.util.AssetHandler;
import org.smartregister.util.FormUtils;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by ndegwamartin on 06/03/2019.
 */
public abstract class BaseChildRegistrationDataFragment extends Fragment {
    protected Map<String, String> childDetails;
    protected View fragmentView;
    private ChildRegistrationDataAdapter mAdapter;
    private List<Field> fields;
    private Map<String, Integer> stringResourceIds;
    private List<String> unformattedNumberFields;

    public ChildRegistrationDataAdapter getmAdapter() {
        return mAdapter;
    }

    public void setmAdapter(ChildRegistrationDataAdapter mAdapter) {
        this.mAdapter = mAdapter;
    }

    public List<Field> getFields() {
        return fields;
    }

    public void setFields(List<Field> fields) {
        this.fields = fields;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Form form = getForm();
        setFields(form.getStep1().getFields());
        unformattedNumberFields = addUnFormattedNumberFields("");
        stringResourceIds = getDataRowLabelResourceIds();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (this.getArguments() != null) {
            Serializable serializable = getArguments().getSerializable(Constants.INTENT_KEY.EXTRA_CHILD_DETAILS);
            if (serializable != null && serializable instanceof CommonPersonObjectClient) {
                childDetails = ((CommonPersonObjectClient) serializable).getColumnmaps();
            }
        }
        // Inflate the layout for this fragment
        fragmentView = inflater.inflate(R.layout.child_registration_data_fragment, container, false);
        return fragmentView;
    }

    protected Form getForm() {
        try {
            return AssetHandler.jsonStringToJava(new FormUtils(getActivity()).getFormJson(getRegistrationForm()).toString(),
                    Form.class);
        } catch (Exception e) {
            Log.e(BaseChildRegistrationDataFragment.class.getCanonicalName(), e.getMessage());
            return null;
        }
    }

    protected abstract Map<String, Integer> getDataRowLabelResourceIds();

    protected abstract String getRegistrationForm();

    public void updateChildDetails(Map<String, String> childDetails) {
        this.childDetails = childDetails;
    }

    public void loadData(Map<String, String> detailsMap) {
        RecyclerView mRecyclerView1 = getActivity().findViewById(R.id.recyclerView);
        resetAdapterData(detailsMap);

        mRecyclerView1.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView1.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView1.setAdapter(mAdapter);

    }

    public void resetAdapterData(Map<String, String> detailsMap) {
        List<KeyValueItem> mArrayList = new ArrayList<>();

        String key;
        String value;

        for (int i = 0; i < getFields().size(); i++) {
            key = getFields().get(i).getKey();
            value = detailsMap.get(key);
            value = !TextUtils.isEmpty(value) ? value : detailsMap.get(getPrefix(getFields().get(i).getEntityId()) +
                    cleanOpenMRSEntityId(getFields().get(i).getOpenmrsEntityId().toLowerCase()));
            String label = cleanLabel(key);

            if (!TextUtils.isEmpty(value) && !TextUtils.isEmpty(label)) {
                mArrayList.add(new KeyValueItem(label, cleanValue(getFields().get(i), value)));
            }

        }

        setmAdapter(new ChildRegistrationDataAdapter(mArrayList));
    }

    public String getPrefix(String entityId) {
        return !TextUtils.isEmpty(entityId) && entityId.equalsIgnoreCase("mother") ? "mother_" : "";
    }

    public String cleanOpenMRSEntityId(String rawEntityId) {
        return Client.birth_date_key.equals(rawEntityId) ? Constants.KEY.DOB : rawEntityId;
    }

    public String cleanLabel(String raw) {
        String label = null;
        if (stringResourceIds != null && stringResourceIds.size() > 0) {
            Integer resourceId = stringResourceIds.get(raw);
            label = resourceId != null ? getResources().getString(resourceId) : null;
        }

        return label;
    }

    public String cleanValue(Field field, String raw) {
        String result = raw;
        String type = field.getType();

        switch (type) {
            case JsonFormConstants.DATE_PICKER:
                Date date = JsonFormUtils.formatDate(raw.contains("T") ? raw.substring(0, raw.indexOf('T')) : raw, false);
                if (date != null) {
                    result = new SimpleDateFormat(com.vijay.jsonwizard.utils.FormUtils.NATIIVE_FORM_DATE_FORMAT_PATTERN)
                            .format(date);
                }
                break;
            case JsonFormConstants.SPINNER:
                if (field.getKeys() != null && field.getKeys().size() > 0 && field.getKeys().contains(raw)) {
                    result = field.getValues().get(field.getKeys().indexOf(raw));
                }

                break;
            case JsonFormConstants.TREE:
                result = LocationHelper.getInstance()
                        .getOpenMrsReadableName(LocationHelper.getInstance().getOpenMrsLocationName(raw));
                break;
            default:
                break;
        }

        if(unformattedNumberFields.contains(field.getKey())){
            result = result.trim();
        }
        else{
            result = cleanResult(result.trim());
        }

        return result;
    }

    private String cleanResult(String result) {
        if (NumberUtils.isNumber(result)) {
            return Utils.formatNumber(result);
        } else {
            return result;
        }
    }

    /**
     * Add number fields that should not be formatted by the number format e.g mothers phone number
     ***/
    protected List<String> addUnFormattedNumberFields(String...key) {
        unformattedNumberFields = new ArrayList<>();
        unformattedNumberFields.addAll(Arrays.asList(key));
        return unformattedNumberFields;
    }


    public void refreshRecyclerViewData(Map<String, String> detailsMap) {
        resetAdapterData(detailsMap);
        mAdapter.notifyDataSetChanged();
    }
}
