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

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.jetbrains.annotations.NotNull;
import org.smartregister.child.ChildLibrary;
import org.smartregister.child.R;
import org.smartregister.child.activity.BaseChildDetailTabbedActivity;
import org.smartregister.child.activity.BaseChildRegisterActivity;
import org.smartregister.child.adapter.ChildRegistrationDataAdapter;
import org.smartregister.child.contract.IChildDetails;
import org.smartregister.child.domain.Field;
import org.smartregister.child.domain.Form;
import org.smartregister.child.domain.KeyValueItem;
import org.smartregister.child.util.ChildJsonFormUtils;
import org.smartregister.child.util.Constants;
import org.smartregister.child.util.Utils;
import org.smartregister.cloudant.models.Client;
import org.smartregister.util.AssetHandler;
import org.smartregister.util.FormUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Created by ndegwamartin on 06/03/2019.
 */
public abstract class BaseChildRegistrationDataFragment extends Fragment {
    protected Map<String, String> childDetails;
    protected View fragmentView;
    private ChildRegistrationDataAdapter mAdapter;
    private List<Field> fields;
    private Map<String, String> stringResourceIds;
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

    /**
     * The map is such that key is the key defined in the registration form json while the value is the strings resource id
     * e.g. Key "First_Name" and Value "R.string.first_name"
     * <p>
     * At runtime, the correct language string will be loaded
     * <p>
     * Values will only show up if you add them here
     */

    protected Map<String, String> getDataRowLabelResourceIds() {

        Map<String, String> resourceIds = new HashMap<>();

        for (Field field : fields) {

            if (field.getHint() != null && !field.getHint().isEmpty()) {
                resourceIds.put(field.getKey(), field.getHint());
            }

        }
        return resourceIds;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (getActivity() instanceof IChildDetails) {
            childDetails = ((IChildDetails) getActivity()).getChildDetails().getColumnmaps();
        }
        // Inflate the layout for this fragment
        fragmentView = inflater.inflate(R.layout.child_registration_data_fragment, container, false);

        Utils.refreshDataCaptureStrategyBanner(this.getActivity(), ((BaseChildDetailTabbedActivity) this.getActivity()).getOpenSRPContext().allSharedPreferences().fetchCurrentLocality());

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
            value = getFieldValue(detailsMap, getFields().get(i), key);

            String label = getResourceLabel(key);

            if (!TextUtils.isEmpty(value) && !TextUtils.isEmpty(label) && !isSkippableValue(value)) {
                mArrayList.add(new KeyValueItem(label, cleanValue(getFields().get(i), value)));
            }

        }

        setmAdapter(new ChildRegistrationDataAdapter(mArrayList));
    }

    private boolean isSkippableValue(String value) {

        List<String> skippableValues = Arrays.asList("[\"Other\"]");

        return skippableValues.contains(value);

    }

    private String getFieldValue(Map<String, String> detailsMap, Field field, String key) {
        String value;
        value = detailsMap.get(getKey(field, key));
        value = !TextUtils.isEmpty(value) ? value : detailsMap.get(getKey(field, key.toLowerCase(Locale.ENGLISH)));
        value = !TextUtils.isEmpty(value) ? value : detailsMap.get(getKey(field, cleanOpenMRSEntityId(field.getOpenmrsEntityId().toLowerCase())));
        return value;
    }

    @NotNull
    private String getKey(Field field, String key) {
        String prefix = getPrefixByEntityId(field.getEntityId());
        return !key.startsWith(prefix) ? prefix + key : key;
    }

    private String getPrefixByEntityId(String entityId) {
        return !TextUtils.isEmpty(entityId) && entityId.equalsIgnoreCase("mother") ? "mother_" : "";
    }

    public String cleanOpenMRSEntityId(String rawEntityId) {
        return Client.birth_date_key.equals(rawEntityId) ? Constants.KEY.DOB : rawEntityId;
    }

    public String getResourceLabel(String raw) {
        String label = null;
        if (stringResourceIds != null && stringResourceIds.size() > 0) {
            label = stringResourceIds.get(raw);
        }

        return label;
    }

    public String cleanValue(Field field, String raw) {
        String result = raw;
        String type = field.getType();

        switch (type) {
            case JsonFormConstants.DATE_PICKER:
                Date date = ChildJsonFormUtils.formatDate(raw.contains("T") ? raw.substring(0, raw.indexOf('T')) : raw, false);
                if (date != null) {
                    result = new SimpleDateFormat(com.vijay.jsonwizard.utils.FormUtils.NATIIVE_FORM_DATE_FORMAT_PATTERN,
                            Locale.getDefault().toString().startsWith("ar") ? Locale.ENGLISH : Locale.getDefault()).format(date);
                }
                break;
            case JsonFormConstants.SPINNER:
                if (field.getKeys() != null && field.getKeys().size() > 0 && field.getKeys().contains(raw)) {
                    result = field.getValues().get(field.getKeys().indexOf(raw));
                } else if (field.getKeys() == null && field.getSubType().equalsIgnoreCase(Constants.JSON_FORM_KEY.LOCATION_SUB_TYPE)) {
                    result = ChildLibrary.getInstance().getLocationRepository().getLocationById(raw).getProperties().getName();
                }

                break;
            case JsonFormConstants.TREE:
                result = ChildLibrary.getInstance().getLocationRepository().getLocationById(raw).getProperties().getName();
                break;
            default:
                break;
        }

        if (unformattedNumberFields.contains(field.getKey())) {
            result = result.trim();
        } else {
            result = cleanResult(result.trim());
        }

        return formatRenderValue(field, result);
    }

    protected String formatRenderValue(Field field, String value) {

        String renderType = StringUtils.isNotBlank(field.getRenderType()) ? field.getRenderType().toLowerCase() : "";
        String result;

        switch (renderType) {
            case "id":
                result = Utils.formatIdentifiers(value);
                break;

            default:
                result = value;
                break;

        }

        return result;


    }

    private String cleanResult(String result) {
        if (NumberUtils.isDigits(result)) {
            return Utils.formatNumber(result);
        } else {
            return result;
        }
    }

    /**
     * Add number fields that should not be formatted by the number format e.g mothers phone number
     ***/
    protected List<String> addUnFormattedNumberFields(String... key) {
        unformattedNumberFields = new ArrayList<>();
        unformattedNumberFields.addAll(Arrays.asList(key));
        return unformattedNumberFields;
    }


    public void refreshRecyclerViewData(Map<String, String> detailsMap) {
        resetAdapterData(detailsMap);
        mAdapter.notifyDataSetChanged();
    }
}
