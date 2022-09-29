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
        ArrayList<String> baseEntityIds = new ArrayList<>();

        appExecutors = new AppExecutorService();
        appExecutors.executorService().execute(() -> {
            try {
                String baseEntityId = childDetails.entityId();
                String motherBaseEntityId = Utils.getValue(childDetails.getColumnmaps(), Constants.KEY.RELATIONAL_ID, false);
                if (!TextUtils.isEmpty(motherBaseEntityId) && !TextUtils.isEmpty(baseEntityId)) {
                    List<HashMap<String, String>> childList = ChildLibrary.getInstance().eventClientRepository()
                            .rawQuery(
                                    ChildLibrary.getInstance().getRepository().getReadableDatabase(),
                                    Utils.metadata().getRegisterQueryProvider().mainRegisterQuery()
                                            + " WHERE " + Utils.metadata().getRegisterQueryProvider().getChildDetailsTable() + ".relational_id IN ('" + motherBaseEntityId + "')"
                            );

                    List<CommonPersonObject> children = new ArrayList<>();
                    for (HashMap<String, String> hashMap : childList) {
                        CommonPersonObject commonPersonObject = new CommonPersonObject(hashMap.get(Constants.KEY.BASE_ENTITY_ID), hashMap.get(Constants.KEY.RELATIONALID), hashMap, Constants.ENTITY.CHILD);
                        commonPersonObject.setColumnmaps(hashMap);
                        children.add(commonPersonObject);
                    }

                    if (children != null && children.size() > 0) {
                        for (CommonPersonObject curChild : children) {
                            if (!baseEntityId.equals(curChild.getCaseId()) && curChild.getColumnmaps().get(Constants.KEY.DOD) == null) {
                                baseEntityIds.add(curChild.getCaseId());
                            }
                        }
                    }
                }
            } catch (Exception e) {
                Timber.e(e);
            }

            appExecutors.mainThread().execute(() -> onTaskResult(baseEntityIds));
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
}

