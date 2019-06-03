package org.smartregister.child.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;

import org.smartregister.child.R;
import org.smartregister.child.util.Constants;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.view.customcontrols.CustomFontTextView;

import java.io.Serializable;
import java.util.Map;

/**
 * Created by ndegwamartin on 06/03/2019.
 */
public abstract class BaseChildRegistrationDataFragment extends Fragment {
    protected Map<String, String> childDetails;
    protected View fragmentView;


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

    public abstract void loadData(Map<String, String> detailsMap);

    /**
     * @since 2019-04-30
     * This method hides registration data fields with empty values
     */
    public void removeEmptyValueFields() {
        // check all textviews in the registration data table
        TableLayout tableLayout = fragmentView.findViewById(R.id.registration_data_table);
        for (int i = 0; i < tableLayout.getChildCount(); i++) {
            TableRow tableRow = (TableRow) tableLayout.getChildAt(i);
            String value = ((CustomFontTextView) tableRow.getChildAt(1)).getText().toString().trim();
            // if no data, hide the row
            if (TextUtils.isEmpty(value) || "kg".equals(value)) {
                tableRow.setVisibility(View.GONE);
            }
        }
    }
}
