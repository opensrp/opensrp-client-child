package org.smartregister.child.task;

import android.text.TextUtils;

import org.smartregister.child.ChildLibrary;
import org.smartregister.child.contract.IGetSiblings;
import org.smartregister.child.util.Constants;
import org.smartregister.child.util.Utils;
import org.smartregister.commonregistry.CommonPersonObject;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.util.AppExecutorService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import timber.log.Timber;

/**
 * Created by ndegwamartin on 08/09/2020.
 */
public class GetSiblingsTask implements OnTaskExecutedActions<ArrayList<String>> {

    private CommonPersonObjectClient childDetails;
    private IGetSiblings getSiblingsInterface;
    private AppExecutorService appExecutors;

    public GetSiblingsTask(CommonPersonObjectClient childDetails, IGetSiblings iGetSiblings) {
        this.childDetails = childDetails;
        this.getSiblingsInterface = iGetSiblings;
    }

    @Override
    public void onTaskStarted() {
        // notify on UI
    }

    @Override
    public void execute() {
        appExecutors = new AppExecutorService();
        appExecutors.executorService().execute(() -> {
            ArrayList<String> siblings = getSiblings(childDetails.entityId());

            appExecutors.mainThread().execute(() -> onTaskResult(siblings));
        });
    }

    @Override
    public void onTaskResult(ArrayList<String> baseEntityIds) {
        List<String> ids = new ArrayList<>();
        if (baseEntityIds != null) {
            ids = baseEntityIds;
        }

        Collections.reverse(ids);

        getSiblingsInterface.onSiblingsFetched(ids);
    }

    private ArrayList<String> getSiblings(String childBaseEntityId) {
        ArrayList<String> siblingBaseEntityIds = new ArrayList<>();

        try {
            String motherBaseEntityId = Utils.getValue(childDetails.getColumnmaps(), Constants.KEY.RELATIONAL_ID, false);
            if (!TextUtils.isEmpty(motherBaseEntityId) && !TextUtils.isEmpty(childBaseEntityId)) {
                List<HashMap<String, String>> childList = ChildLibrary.getInstance().eventClientRepository()
                        .rawQuery(
                                ChildLibrary.getInstance().getRepository().getReadableDatabase(),
                                Utils.metadata().getRegisterQueryProvider().mainRegisterQuery()
                                        + " WHERE " + Utils.metadata().getRegisterQueryProvider().getChildDetailsTable() + ".relational_id IN ('" + motherBaseEntityId + "')"
                        );

                List<CommonPersonObject> siblings = new ArrayList<>();
                for (HashMap<String, String> hashMap : childList) {
                    CommonPersonObject commonPersonObject = new CommonPersonObject(
                            hashMap.get(Constants.KEY.BASE_ENTITY_ID),
                            hashMap.get(Constants.KEY.RELATIONALID),
                            hashMap,
                            Constants.ENTITY.CHILD
                    );
                    commonPersonObject.setColumnmaps(hashMap);
                    siblings.add(commonPersonObject);
                }

                if (siblings.size() > 0) {
                    for (CommonPersonObject sibling : siblings) {
                        if (!childBaseEntityId.equals(sibling.getCaseId()) && sibling.getColumnmaps().get(Constants.KEY.DOD) == null) {
                            siblingBaseEntityIds.add(sibling.getCaseId());
                        }
                    }
                }
            }
        } catch (Exception e) {
            Timber.e(e);
        }

        return siblingBaseEntityIds;
    }
}
