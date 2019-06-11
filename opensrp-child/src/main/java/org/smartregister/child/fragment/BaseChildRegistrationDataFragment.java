package org.smartregister.child.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.smartregister.child.R;
import org.smartregister.child.adapter.ChildRegistrationDataAdapter;
import org.smartregister.child.domain.KeyValueItem;
import org.smartregister.child.util.Constants;
import org.smartregister.commonregistry.CommonPersonObjectClient;

import java.io.Serializable;
import java.util.ArrayList;
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


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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

    private void resetAdapterData(Map<String, String> detailsMap) {
        List<KeyValueItem> mArrayList = new ArrayList<>();

        for (Map.Entry<String, String> entry : detailsMap.entrySet()) {

            mArrayList.add(new KeyValueItem(entry.getKey(), entry.getValue()));
        }

        mAdapter = new ChildRegistrationDataAdapter(mArrayList);
    }


    public void refreshRecyclerViewData(Map<String, String> detailsMap) {

        resetAdapterData(detailsMap);
        mAdapter.notifyDataSetChanged();
    }
}

