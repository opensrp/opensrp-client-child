package org.smartregister.child.presenter;

import androidx.annotation.VisibleForTesting;

import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.joda.time.DateTime;
import org.smartregister.child.activity.BaseChildDetailTabbedActivity;
import org.smartregister.child.contract.ChildUnderFiveFragmentContract;
import org.smartregister.child.domain.WrapperParam;
import org.smartregister.child.util.Constants;
import org.smartregister.child.util.Utils;
import org.smartregister.domain.Photo;
import org.smartregister.domain.ProfileImage;
import org.smartregister.growthmonitoring.GrowthMonitoringLibrary;
import org.smartregister.growthmonitoring.domain.Height;
import org.smartregister.growthmonitoring.domain.HeightWrapper;
import org.smartregister.growthmonitoring.domain.Weight;
import org.smartregister.growthmonitoring.domain.WeightWrapper;
import org.smartregister.growthmonitoring.repository.HeightRepository;
import org.smartregister.growthmonitoring.repository.WeightRepository;
import org.smartregister.immunization.ImmunizationLibrary;
import org.smartregister.immunization.util.ImageUtils;
import org.smartregister.repository.ImageRepository;
import org.smartregister.util.DateUtil;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static org.smartregister.util.Utils.getValue;

/**
 * Created by ndegwamartin on 01/12/2020.
 */
public class ChildUnderFiveFragmentPresenter implements ChildUnderFiveFragmentContract.Presenter {


    @Override
    public Photo getProfilePhotoByClient(Map<String, String> detailsMap) {

        Photo photo = new Photo();
        ProfileImage profileImage = getImageRepository().findByEntityId(Utils.getValue(detailsMap, Constants.KEY.BASE_ENTITY_ID, false));
        if (profileImage != null) {
            photo.setFilePath(profileImage.getFilepath());
        } else {
            String gender = getValue(detailsMap, Constants.KEY.GENDER, true);
            photo.setResourceId(ImageUtils.profileImageResourceByGender(gender));
        }
        return photo;

    }

    protected ImageRepository getImageRepository() {
        return ImmunizationLibrary.getInstance().context().imageRepository();
    }

    @Override
    public String constructChildName(Map<String, String> detailsMap) {
        String firstName = Utils.getValue(detailsMap, Constants.KEY.FIRST_NAME, true);
        String lastName = Utils.getValue(detailsMap, Constants.KEY.LAST_NAME, true);
        return Utils.getName(firstName, lastName).trim();
    }

    @NotNull
    @Override
    public List<Weight> getWeights(String baseEntityId, List<Weight> weights) {
        List<Weight> weightList = new ArrayList<>();
        List<Weight> formattedWeights = getChildSpecificWeights(baseEntityId, weights);
        if (!formattedWeights.isEmpty()) {
            if (formattedWeights.size() <= 5) {
                weightList = formattedWeights;
            } else {
                weightList = formattedWeights.subList(0, 5);
            }
        }

        return weightList;
    }

    @NotNull
    @Override
    public List<Height> getHeights(String baseEntityId, List<Height> heights) {
        List<Height> heightList = new ArrayList<>();
        List<Height> formattedHeights = getChildSpecificHeights(baseEntityId, heights);
        if (!formattedHeights.isEmpty()) {
            if (heights.size() <= 5) {
                heightList = heights;
            } else {
                heightList = heights.subList(0, 5);
            }
        }

        return heightList;
    }

    @Override
    public List<Height> getChildSpecificHeights(String baseEntityId, List<Height> heights) {
        List<Height> heightList = new ArrayList<>();
        for (Height height : heights) {
            if (height.getBaseEntityId().equals(baseEntityId)) {
                heightList.add(height);
            }
        }

        return heightList;
    }

    @Override
    public List<Weight> getChildSpecificWeights(String baseEntityId, List<Weight> weights) {
        List<Weight> weightList = new ArrayList<>();
        for (Weight weight : weights) {
            if (weight.getBaseEntityId().equals(baseEntityId)) {
                weightList.add(weight);
            }
        }

        return weightList;
    }

    @Override
    public Date getBirthDate(Map<String, String> detailsMap) {
        String birthDate = Utils.getValue(detailsMap, Constants.KEY.DOB, false);
        return Utils.dobStringToDate(birthDate);
    }

    @Override
    public WeightWrapper getWeightWrapper(WrapperParam weightParams) {
        WeightWrapper weightWrapper = new WeightWrapper();
        weightWrapper.setId(weightParams.getBaseEntityId());

        List<Weight> weightList = getWeightRepository().findByEntityId(weightParams.getBaseEntityId());
        Weight weight = getWeight(weightList, weightParams.getPosition());

        if (!weightList.isEmpty()) {
            weightWrapper.setWeight(weight.getKg());
            weightWrapper.setUpdatedWeightDate(new DateTime(weight.getDate()), false);
            weightWrapper.setDbKey(weight.getId());
        }

        weightWrapper.setGender(weightParams.getGender());
        weightWrapper.setPatientName(weightParams.getChildName());
        weightWrapper.setPatientNumber(weightParams.getOpenSrpId());
        weightWrapper.setPatientAge(weightParams.getDuration());
        weightWrapper.setPhoto(weightParams.getPhoto());
        weightWrapper.setPmtctStatus(weightParams.getPmtctStatus());
        return weightWrapper;
    }

    @VisibleForTesting
    protected WeightRepository getWeightRepository() {
        return GrowthMonitoringLibrary.getInstance().weightRepository();
    }

    @Override
    public Weight getWeight(List<Weight> weights, long dateRecordedTimestamp) {
        Weight displayWeight = new Weight();
        for (Weight weight : weights) {
            if (Utils.isSameDay(dateRecordedTimestamp, weight.getDate().getTime(), null)) {
                displayWeight = weight;
            }
        }

        return displayWeight;
    }

    @Override
    public HeightWrapper getHeightWrapper(WrapperParam heightParam) {
        HeightWrapper heightWrapper = new HeightWrapper();
        heightWrapper.setId(heightParam.getBaseEntityId());

        List<Height> heightList = getHeightRepository().findByEntityId(heightParam.getBaseEntityId());
        Height height = getHeight(heightList, heightParam.getPosition());

        if (!heightList.isEmpty()) {
            heightWrapper.setHeight(height.getCm());
            heightWrapper.setUpdatedHeightDate(new DateTime(height.getDate()), false);
            heightWrapper.setDbKey(height.getId());
        }

        heightWrapper.setGender(heightParam.getGender());
        heightWrapper.setPatientName(heightParam.getChildName());
        heightWrapper.setPatientNumber(heightParam.getOpenSrpId());
        heightWrapper.setPatientAge(heightParam.getDuration());
        heightWrapper.setPhoto(heightParam.getPhoto());
        heightWrapper.setPmtctStatus(heightParam.getPmtctStatus());
        return heightWrapper;
    }

    protected HeightRepository getHeightRepository() {
        return GrowthMonitoringLibrary.getInstance().heightRepository();
    }

    @Override
    public Height getHeight(List<Height> heights, long dateRecordedTimestamp) {
        Height displayHeight = new Height();
        for (Height height : heights) {
            if (Utils.isSameDay(dateRecordedTimestamp, height.getDate().getTime(), null)) {
                displayHeight = height;
            }
        }

        return displayHeight;
    }

    @Override
    public void sortTheWeightsInDescendingOrder(List<Weight> weightList) {
        Collections.sort(weightList, (o1, o2) -> (o1 != null && o2 != null && o2.getDate() != null) ? o2.getDate().compareTo(o1.getDate()) : 0);
    }

    @Override
    public void sortTheHeightsInDescendingOrder(List<Height> heightList) {
        Collections.sort(heightList, (o1, o2) -> (o1 != null && o2 != null && o2.getDate() != null) ? o2.getDate().compareTo(o1.getDate()) : 0);
    }

    @Override
    @NotNull
    public WrapperParam getWrapperParam(Map<String, String> detailsMap, long growthRecordPosition) {
        String childName = constructChildName(detailsMap);
        String gender = Utils.getValue(detailsMap, Constants.KEY.GENDER, true);
        String motherFirstName = Utils.getValue(detailsMap, Constants.KEY.MOTHER_FIRST_NAME, true);
        if (StringUtils.isBlank(childName) && StringUtils.isNotBlank(motherFirstName)) {
            childName = "B/o " + motherFirstName.trim();
        }
        String openSrpId = Utils.getValue(detailsMap, Constants.KEY.ZEIR_ID, false);
        String duration = "";
        String dobString = Utils.getValue(detailsMap, Constants.KEY.DOB, false);
        DateTime dateTime = Utils.dobStringToDateTime(dobString);

        Date dob = null;
        if (dateTime != null) {
            duration = getChildAge(dateTime);
            dob = dateTime.toDate();
        }

        if (dob == null) {
            dob = Calendar.getInstance().getTime();
        }

        Photo photo = getProfilePhotoByClient(detailsMap);

        String pmtctStatus = Utils.getValue(detailsMap, BaseChildDetailTabbedActivity.PMTCT_STATUS_LOWER_CASE, false);

        WrapperParam wrapperParams = new WrapperParam();
        wrapperParams.setBaseEntityId(Utils.getValue(detailsMap, Constants.KEY.BASE_ENTITY_ID, false));
        wrapperParams.setChildName(childName);
        wrapperParams.setPosition(growthRecordPosition);
        wrapperParams.setDuration(duration);
        wrapperParams.setGender(gender);
        wrapperParams.setOpenSrpId(openSrpId);
        wrapperParams.setPhoto(photo);
        wrapperParams.setPmtctStatus(pmtctStatus);
        wrapperParams.setDob(dob);
        return wrapperParams;
    }

    @VisibleForTesting
    protected String getChildAge(DateTime dateTime) {
        return DateUtil.getDuration(dateTime);
    }
}
