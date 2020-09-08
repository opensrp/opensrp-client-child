package org.smartregister.child.provider;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import org.joda.time.DateTime;
import org.smartregister.child.R;
import org.smartregister.child.util.Constants;
import org.smartregister.child.util.DBConstants;
import org.smartregister.commonregistry.CommonPersonObject;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.util.Utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

/**
 * Created by Ahmed on 13-Oct-15.
 */
public class MotherLookUpSmartClientsProvider {
    private final LayoutInflater inflater;
    private final DateFormat dateFormat = new SimpleDateFormat("dd/MM/yy", Locale.ENGLISH);


    public MotherLookUpSmartClientsProvider(Context context) {
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void getView(CommonPersonObject commonPersonObject, List<CommonPersonObject> children, View convertView) {
        CommonPersonObjectClient pc = Utils.convert(commonPersonObject);

        List<CommonPersonObjectClient> childList = new ArrayList<>();
        for (CommonPersonObject childCommonPersonObject : children) {
            childList.add(Utils.convert(childCommonPersonObject));
        }

        String childName = name(pc);

        Utils.fillValue((TextView) convertView.findViewById(R.id.name), childName);

        String birthDateString = "Birthdate or contact phone number missing";

        String contactPhoneNumber = pc.getColumnmaps().get(DBConstants.KEY.MOTHER_GUARDIAN_PHONE_NUMBER);//To do refactor for configurability
        if (contactPhoneNumber == null) {

            DateTime birthDateTime = dob(pc);
            if (birthDateTime != null) {
                birthDateString = dateFormat.format(birthDateTime.toDate());
            }
        } else {
            birthDateString = contactPhoneNumber;
        }

        String childListString = "";
        if (!childList.isEmpty()) {
            if (childList.size() > 1) {
                //sort youngest to oldest
                sortList(childList);
            }

            for (int i = 0; i < childList.size(); i++) {
                String name = name(childList.get(i));
                if (i == (childList.size() - 1)) {
                    childListString += name;
                } else {
                    childListString += name + ", ";
                }
            }

        }

        Utils.fillValue((TextView) convertView.findViewById(R.id.details), birthDateString + " - " + childListString);
    }

    private String name(CommonPersonObjectClient pc) {
        String firstName = Utils.getValue(pc.getColumnmaps(), Constants.KEY.FIRST_NAME, true);
        String lastName = Utils.getValue(pc.getColumnmaps(), Constants.KEY.LAST_NAME, true);
        return Utils.getName(firstName, lastName);
    }

    private DateTime dob(CommonPersonObjectClient pc) {
        String dobString = Utils.getValue(pc.getColumnmaps(), Constants.KEY.DOB, false);
        return Utils.dobStringToDateTime(dobString);
    }

    private void sortList(List<CommonPersonObjectClient> childList) {
        Collections.sort(childList, new Comparator<CommonPersonObjectClient>() {
            @Override
            public int compare(CommonPersonObjectClient lhs, CommonPersonObjectClient rhs) {
                DateTime lhsTime = dob(lhs);
                DateTime rhsTime = dob(rhs);

                if (lhsTime == null && rhsTime == null) {
                    return 0;
                } else if (lhsTime == null) {
                    return 1;
                } else if (rhsTime == null) {
                    return -1;
                }

                long diff = lhsTime.getMillis() - rhsTime.getMillis();
                if (diff > 0) {
                    return -1;
                } else if (diff < 0) {
                    return 1;
                }

                return 0;
            }
        });
    }

    public View inflateLayoutForCursorAdapter() {
        return inflater().inflate(R.layout.mother_child_lookup_client, null);
    }

    private LayoutInflater inflater() {
        return inflater;
    }

}
