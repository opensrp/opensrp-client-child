package org.smartregister.child.presenter;

import android.text.TextUtils;

import org.jetbrains.annotations.NotNull;
import org.smartregister.Context;
import org.smartregister.child.contract.ChildImmunizationContract;
import org.smartregister.child.util.ChildJsonFormUtils;
import org.smartregister.child.util.Constants;
import org.smartregister.child.util.Utils;
import org.smartregister.commonregistry.CommonPersonObject;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.growthmonitoring.GrowthMonitoringLibrary;
import org.smartregister.growthmonitoring.domain.Height;
import org.smartregister.growthmonitoring.domain.Weight;
import org.smartregister.location.helper.LocationHelper;

import java.lang.ref.WeakReference;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import timber.log.Timber;

/**
 * Created by ndegwamartin on 01/09/2020.
 */
public class BaseChildImmunizationPresenter implements ChildImmunizationContract.Presenter {

    private WeakReference<ChildImmunizationContract.View> viewReference;

    public BaseChildImmunizationPresenter(ChildImmunizationContract.View view) {
        this.viewReference = new WeakReference<>(view);
    }

    public ChildImmunizationContract.View getView() {
        if (viewReference != null) return viewReference.get();
        else return null;
    }

    @NotNull
    private Weight getWeight(Date dob, Double birthWeight) {
        Weight weight = new Weight();
        weight.setId(-1L);
        weight.setBaseEntityId(null);
        weight.setKg((float) birthWeight.doubleValue());
        weight.setDate(dob);
        weight.setAnmId(null);
        weight.setLocationId(null);
        weight.setSyncStatus(null);
        weight.setUpdatedAt(Calendar.getInstance().getTimeInMillis());
        weight.setEventId(null);
        weight.setFormSubmissionId(null);
        weight.setOutOfCatchment(0);
        return weight;
    }

    @NotNull
    private Height getHeight(Date dob, Double birthHeight) {
        Height height = new Height();
        height.setId(-1L);
        height.setBaseEntityId(null);
        height.setCm((float) birthHeight.doubleValue());
        height.setDate(dob);
        height.setAnmId(null);
        height.setLocationId(null);
        height.setSyncStatus(null);
        height.setUpdatedAt(Calendar.getInstance().getTimeInMillis());
        height.setEventId(null);
        height.setFormSubmissionId(null);
        height.setOutOfCatchment(0);
        return height;
    }

    public List<Height> getAllHeights(CommonPersonObjectClient childDetails) {
        List<Height> allHeights = GrowthMonitoringLibrary.getInstance().heightRepository().findByEntityId(childDetails.entityId());
        try {
            String dobString = Utils.getValue(childDetails.getColumnmaps(), Constants.KEY.DOB, false);
            Date dob = Utils.dobStringToDate(dobString);
            if (!TextUtils.isEmpty(Utils.getValue(childDetails.getColumnmaps(), Constants.KEY.BIRTH_HEIGHT, false)) &&
                    dob != null) {
                Double birthHeight =
                        Double.valueOf(Utils.getValue(childDetails.getColumnmaps(), Constants.KEY.BIRTH_HEIGHT, false));

                Height height = getHeight(dob, birthHeight);
                allHeights.add(height);
            }
        } catch (Exception e) {
            Timber.e(e);
        }

        return allHeights;
    }

    @Override
    public void activateChildStatus(Context openSRPcontext, CommonPersonObjectClient childDetails) {
        try {
            Map<String, String> details = Utils.getEcChildDetails(childDetails.entityId()).getColumnmaps();
            CommonPersonObject commonPersonObject = new CommonPersonObject(details.get(Constants.KEY.BASE_ENTITY_ID), details.get(Constants.KEY.RELATIONALID), details, "child");
            if (details.containsKey(Constants.CHILD_STATUS.INACTIVE) &&
                    details.get(Constants.CHILD_STATUS.INACTIVE) != null &&
                    details.get(Constants.CHILD_STATUS.INACTIVE).equalsIgnoreCase(Boolean.TRUE.toString())) {
                commonPersonObject.setColumnmaps(
                        ChildJsonFormUtils.updateClientAttribute(openSRPcontext, childDetails, LocationHelper.getInstance(), Constants.CHILD_STATUS.INACTIVE, false));
            }

            if (details.containsKey(Constants.CHILD_STATUS.LOST_TO_FOLLOW_UP) &&
                    details.get(Constants.CHILD_STATUS.LOST_TO_FOLLOW_UP) != null &&
                    details.get(Constants.CHILD_STATUS.LOST_TO_FOLLOW_UP).equalsIgnoreCase(Boolean.TRUE.toString())) {
                commonPersonObject.setColumnmaps(ChildJsonFormUtils.updateClientAttribute(openSRPcontext, childDetails, LocationHelper.getInstance(), Constants.CHILD_STATUS.LOST_TO_FOLLOW_UP, false));
            }
        } catch (Exception e) {
            Timber.e(e);
        }
    }

    public List<Weight> getAllWeights(CommonPersonObjectClient childDetails) {
        List<Weight> allWeights = GrowthMonitoringLibrary.getInstance().weightRepository().findByEntityId(childDetails.entityId());
        try {
            String dobString = Utils.getValue(childDetails.getColumnmaps(), Constants.KEY.DOB, false);
            Date dob = Utils.dobStringToDate(dobString);
            if (!TextUtils.isEmpty(Utils.getValue(childDetails.getColumnmaps(), Constants.KEY.BIRTH_WEIGHT, false)) &&
                    dob != null) {
                Double birthWeight =
                        Double.valueOf(Utils.getValue(childDetails.getColumnmaps(), Constants.KEY.BIRTH_WEIGHT, false));

                Weight weight = getWeight(dob, birthWeight);
                allWeights.add(weight);
            }
        } catch (Exception e) {
            Timber.e(e);
        }

        return allWeights;
    }
}
