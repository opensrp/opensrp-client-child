package org.smartregister.child.task;

import android.os.AsyncTask;
import android.text.TextUtils;

import org.smartregister.child.ChildLibrary;
import org.smartregister.child.contract.IGetSiblings;
import org.smartregister.child.util.Constants;
import org.smartregister.child.util.Utils;
import org.smartregister.commonregistry.CommonPersonObject;
import org.smartregister.commonregistry.CommonPersonObjectClient;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * Created by ndegwamartin on 08/09/2020.
 */

public class GetSiblingsTask extends AsyncTask<Void, Void, ArrayList<String>> {

    private CommonPersonObjectClient childDetails;
    private IGetSiblings getSiblingsInterface;

    public GetSiblingsTask(CommonPersonObjectClient childDetails, IGetSiblings iGetSiblings) {
        this.childDetails = childDetails;
        this.getSiblingsInterface = iGetSiblings;

    }

    @Override
    protected ArrayList<String> doInBackground(Void... params) {
        String baseEntityId = childDetails.entityId();
        String motherBaseEntityId = Utils.getValue(childDetails.getColumnmaps(), Constants.KEY.RELATIONAL_ID, false);
        if (!TextUtils.isEmpty(motherBaseEntityId) && !TextUtils.isEmpty(baseEntityId)) {

            String whereClause = constructWhereClause(motherBaseEntityId);
            List<HashMap<String, String>> childList = ChildLibrary.getInstance()
                    .eventClientRepository()
                    .rawQuery(ChildLibrary.getInstance().getRepository().getReadableDatabase(),
                            Utils.metadata().getRegisterQueryProvider().mainRegisterQuery() + whereClause);


            List<CommonPersonObject> children = new ArrayList<>();
            for (HashMap<String, String> hashMap : childList) {
                CommonPersonObject commonPersonObject = new CommonPersonObject(hashMap.get(Constants.KEY.BASE_ENTITY_ID), hashMap.get(Constants.KEY.RELATIONALID), hashMap, Constants.ENTITY.CHILD);
                commonPersonObject.setColumnmaps(hashMap);
                children.add(commonPersonObject);
            }

            if (children != null && children.size() > 0) {
                ArrayList<String> baseEntityIds = new ArrayList<>();
                for (CommonPersonObject curChild : children) {
                    if (!baseEntityId.equals(curChild.getCaseId()) && curChild.getColumnmaps().get(Constants.KEY.DOD) == null) {
                        baseEntityIds.add(curChild.getCaseId());
                    }
                }

                return baseEntityIds;
            }
        }
        return null;
    }

    private String constructWhereClause(String motherBaseEntityId) {
        return " WHERE " + Utils.metadata().getRegisterQueryProvider().getChildDetailsTable() + ".relational_id IN ('" + motherBaseEntityId + "') AND " +
                Utils.metadata().getRegisterQueryProvider().getDemographicTable() + ".date_removed IS NULL  AND " +
                Utils.metadata().getRegisterQueryProvider().getDemographicTable() + ".dod IS NULL AND " +
                Utils.metadata().getRegisterQueryProvider().getDemographicTable() + ".is_closed = 0";
    }

    @Override
    protected void onPostExecute(ArrayList<String> baseEntityIds) {
        super.onPostExecute(baseEntityIds);
        List<String> ids = new ArrayList<>();
        if (baseEntityIds != null) {
            ids = baseEntityIds;
        }

        Collections.reverse(ids);

        getSiblingsInterface.onSiblingsFetched(ids);
    }
}

