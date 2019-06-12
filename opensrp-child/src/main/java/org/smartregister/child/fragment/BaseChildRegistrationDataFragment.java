package org.smartregister.child.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.vijay.jsonwizard.constants.JsonFormConstants;

import org.smartregister.child.R;
import org.smartregister.child.adapter.ChildRegistrationDataAdapter;
import org.smartregister.child.domain.Field;
import org.smartregister.child.domain.Form;
import org.smartregister.child.domain.KeyValueItem;
import org.smartregister.child.util.Constants;
import org.smartregister.child.util.JsonFormUtils;
import org.smartregister.commonregistry.CommonPersonObjectClient;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by ndegwamartin on 06/03/2019.
 */
public abstract class BaseChildRegistrationDataFragment extends Fragment {
    protected Map<String, String> childDetails;
    protected View fragmentView;
    private RecyclerView mRecyclerView1;
    private ChildRegistrationDataAdapter mAdapter;
    private List<Field> fields;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Form form = getForm();
        fields = form.getStep1().getFields();
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


    public void updateChildDetails(Map<String, String> childDetails) {
        this.childDetails = childDetails;
    }

    public void loadData(Map<String, String> detailsMap) {

        mRecyclerView1 = getActivity().findViewById(R.id.recyclerView);

        resetAdapterData(detailsMap);

        mRecyclerView1.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView1.setItemAnimator(new DefaultItemAnimator());
        //  mRecyclerView1.addItemDecoration(new DividerItemDecoration(getActivity(), LinearLayoutManager.VERTICAL));
        mRecyclerView1.setAdapter(mAdapter);

    }

    protected abstract Form getForm();

    private void resetAdapterData(Map<String, String> detailsMap) {
        List<KeyValueItem> mArrayList = new ArrayList<>();

        String key;
        String value;

        for (int i = 0; i < fields.size(); i++) {
            key = fields.get(i).getKey();

            value = detailsMap.get(key);
            value = !TextUtils.isEmpty(value) ? value : detailsMap.get(fields.get(i).getOpenmrsEntityId());

            if (!TextUtils.isEmpty(value)) {
                mArrayList.add(new KeyValueItem(cleanKey(key), cleanValue(fields.get(i).getType(), value)));
            }

        }

        mAdapter = new ChildRegistrationDataAdapter(mArrayList);
    }

    private String cleanKey(String raw) {
        return raw.replaceAll("_", " ");
    }

    private String cleanValue(String type, String raw) {
        String result = raw;
        switch (type) {
            case JsonFormConstants.DATE_PICKER:
                Date date = JsonFormUtils.formatDate(raw, false);
                if (date != null) {
                    result = Constants.DATE_FORMAT.format(date);
                }
                break;

            default:
                break;

        }

        return result;
    }

    public void refreshRecyclerViewData(Map<String, String> detailsMap) {

        resetAdapterData(detailsMap);
        mAdapter.notifyDataSetChanged();
    }
}

