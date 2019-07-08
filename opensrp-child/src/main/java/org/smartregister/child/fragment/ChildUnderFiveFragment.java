package org.smartregister.child.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TableRow;
import android.widget.TextView;

import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.joda.time.DateTime;
import org.smartregister.child.R;
import org.smartregister.child.activity.BaseChildDetailTabbedActivity;
import org.smartregister.child.util.Constants;
import org.smartregister.child.view.WidgetFactory;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.domain.Alert;
import org.smartregister.domain.Photo;
import org.smartregister.growthmonitoring.GrowthMonitoringLibrary;
import org.smartregister.growthmonitoring.domain.Height;
import org.smartregister.growthmonitoring.domain.HeightWrapper;
import org.smartregister.growthmonitoring.domain.Weight;
import org.smartregister.growthmonitoring.domain.WeightWrapper;
import org.smartregister.growthmonitoring.fragment.EditGrowthDialogFragment;
import org.smartregister.growthmonitoring.util.HeightUtils;
import org.smartregister.growthmonitoring.util.WeightUtils;
import org.smartregister.immunization.ImmunizationLibrary;
import org.smartregister.immunization.domain.ServiceRecord;
import org.smartregister.immunization.domain.ServiceType;
import org.smartregister.immunization.domain.ServiceWrapper;
import org.smartregister.immunization.domain.Vaccine;
import org.smartregister.immunization.domain.VaccineWrapper;
import org.smartregister.immunization.domain.jsonmapping.VaccineGroup;
import org.smartregister.immunization.fragment.ServiceEditDialogFragment;
import org.smartregister.immunization.fragment.VaccinationEditDialogFragment;
import org.smartregister.immunization.util.ImageUtils;
import org.smartregister.immunization.util.VaccinateActionUtils;
import org.smartregister.immunization.util.VaccinatorUtils;
import org.smartregister.immunization.view.ImmunizationRowGroup;
import org.smartregister.immunization.view.ServiceRowGroup;
import org.smartregister.util.DateUtil;
import org.smartregister.util.Utils;
import org.smartregister.view.customcontrols.CustomFontTextView;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.smartregister.child.util.Utils.updateGrowthValue;

/**
 * Created by ndegwamartin on 06/03/2019.
 */
public class ChildUnderFiveFragment extends Fragment {

    private static final String DIALOG_TAG = "ChildImmunoActivity_DIALOG_TAG";
    private LayoutInflater inflater;
    private CommonPersonObjectClient childDetails;
    private Map<String, String> detailsMap;
    private LinearLayout fragmentContainer;

    private Boolean curVaccineMode;
    private Boolean curServiceMode;
    private Boolean curGrowthMonitoringMode;

    public ChildUnderFiveFragment() {
        // Required empty public constructor
    }

    public static ChildUnderFiveFragment newInstance(Bundle bundle) {
        Bundle args = bundle;
        ChildUnderFiveFragment fragment = new ChildUnderFiveFragment();
        if (args == null) {
            args = new Bundle();
        }
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.inflater = inflater;
        if (this.getArguments() != null) {
            Serializable serializable = getArguments().getSerializable(Constants.INTENT_KEY.EXTRA_CHILD_DETAILS);
            if (serializable != null && serializable instanceof CommonPersonObjectClient) {
                childDetails = (CommonPersonObjectClient) serializable;
                detailsMap = childDetails.getColumnmaps();
            }
        }
        View underFiveFragment = inflater.inflate(R.layout.child_under_five_fragment, container, false);
        fragmentContainer = underFiveFragment.findViewById(R.id.container);

        return underFiveFragment;
    }

    public void setDetailsMap(Map<String, String> detailsMap) {
        this.detailsMap = detailsMap;
    }

    public void loadGrowthMonitoringView(List<Weight> weightList, List<Height> heightList,
                                         boolean editGrowthMonitoringMode) {
        boolean showGrowthMonitoring =
                curGrowthMonitoringMode == null || !curGrowthMonitoringMode.equals(editGrowthMonitoringMode);
        if (fragmentContainer != null && showGrowthMonitoring) {
            createGrowthLayout(weightList, heightList, fragmentContainer, editGrowthMonitoringMode);
            curGrowthMonitoringMode = editGrowthMonitoringMode;
        }
    }

    private void createGrowthLayout(List<Weight> weights, List<Height> heights, LinearLayout fragmentContainer,
                                    boolean editMode) {
        ArrayList<Boolean> weightEditMode = new ArrayList<>();
        ArrayList<Boolean> heightEditMode = new ArrayList<>();
        ArrayList<View.OnClickListener> listeners = new ArrayList<>();

        List<Weight> weightList = getWeights(weights);
        List<Height> heightList = getHeights(heights);

        LinkedHashMap<Long, Pair<String, String>> weightMap =
                updateWeightMap(editMode, weightEditMode, listeners, weightList);
        LinkedHashMap<Long, Pair<String, String>> heightMap =
                updateHeightMap(editMode, heightEditMode, listeners, heightList);

        WidgetFactory widgetFactory = new WidgetFactory();
        if (weightMap.size() > 0) {
            widgetFactory.createWeightWidget(inflater, fragmentContainer, weightMap, listeners, weightEditMode);
        }

        if (heightMap.size() > 0) {
            widgetFactory.createHeightWidget(inflater, fragmentContainer, heightMap, listeners, heightEditMode);
        }
    }

    private LinkedHashMap<Long, Pair<String, String>> updateWeightMap(boolean editMode, ArrayList<Boolean> weightEditMode,
                                                                      ArrayList<View.OnClickListener> listeners,
                                                                      List<Weight> weightList) {
        LinkedHashMap<Long, Pair<String, String>> weightMap = new LinkedHashMap<>();
        for (int i = 0; i < weightList.size(); i++) {
            Weight weight = weightList.get(i);
            String formattedAge = "";
            if (weight.getDate() != null) {
                Date weightDate = weight.getDate();
                String birthDate = Utils.getValue(detailsMap, Constants.KEY.DOB, false);
                Date birth = Utils.dobStringToDate(birthDate);
                if (birth != null) {
                    long timeDiff = weightDate.getTime() - birth.getTime();
                    Log.v("timeDiff is ", timeDiff + "");
                    if (timeDiff >= 0) {
                        formattedAge = DateUtil.getDuration(timeDiff);
                        Log.v("age is ", formattedAge);
                    }
                }
            }

            if (!formattedAge.equalsIgnoreCase("0d")) {
                weightMap.put(weight.getId() - 1, Pair.create(formattedAge, Utils.kgStringSuffix(
                        org.smartregister.child.util.Utils.formatNumber(String.valueOf(weight.getKg())))));

                boolean lessThanThreeMonthsEventCreated = WeightUtils.lessThanThreeMonths(weight);
                weightEditMode.add(lessThanThreeMonthsEventCreated && editMode);

                final long weightTaken = weight.getId();
                View.OnClickListener onClickListener = new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showGrowthMonitoringDialog((int) weightTaken);
                    }
                };
                listeners.add(onClickListener);
            }

        }

        if (weightMap.size() < 5) {
            weightMap.put(0L, Pair.create(DateUtil.getDuration(0),
                    updateGrowthValue(Utils.getValue(detailsMap, Constants.KEY.BIRTH_WEIGHT, true)) + " kg"));
            weightEditMode.add(false);
            listeners.add(null);
        }

        return weightMap;
    }

    private LinkedHashMap<Long, Pair<String, String>> updateHeightMap(boolean editMode, ArrayList<Boolean> heightEditMode,
                                                                      ArrayList<View.OnClickListener> listeners,
                                                                      List<Height> heightList) {
        LinkedHashMap<Long, Pair<String, String>> heightMap = new LinkedHashMap<>();
        for (int i = 0; i < heightList.size(); i++) {
            Height height = heightList.get(i);
            String formattedAge = "";
            if (height.getDate() != null) {
                Date heightDate = height.getDate();
                Date birth = getBirthDate();
                if (birth != null) {
                    long timeDiff = heightDate.getTime() - birth.getTime();
                    Log.v("timeDiff is ", timeDiff + "");
                    if (timeDiff >= 0) {
                        formattedAge = DateUtil.getDuration(timeDiff);
                        Log.v("age is ", formattedAge);
                    }
                }
            }

            if (!formattedAge.equalsIgnoreCase("0d")) {
                heightMap.put(height.getId() - 1, Pair.create(formattedAge, Utils.cmStringSuffix(height.getCm())));

                boolean lessThanThreeMonthsEventCreated = HeightUtils.lessThanThreeMonths(height);
                heightEditMode.add(lessThanThreeMonthsEventCreated && editMode);

                final long heightTaken = height.getId();
                View.OnClickListener onClickListener = new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showGrowthMonitoringDialog((int) heightTaken);
                    }
                };
                listeners.add(onClickListener);
            }

        }

        String initialHeight = Utils.getValue(detailsMap, Constants.KEY.BIRTH_HEIGHT, true);
        if (heightMap.size() < 5 && !initialHeight.isEmpty()) {
            heightMap.put(0L, Pair.create(DateUtil.getDuration(0), updateGrowthValue(initialHeight) + " cm"));
            heightEditMode.add(false);
            listeners.add(null);
        }

        return heightMap;
    }

    @NotNull
    private List<Weight> getWeights(List<Weight> weights) {
        List<Weight> weightList = new ArrayList<>();
        List<Weight> formattedWeights = getChildSpecificWeights(weights);
        if (formattedWeights != null && !formattedWeights.isEmpty()) {
            if (formattedWeights.size() <= 5) {
                weightList = formattedWeights;
            } else {
                weightList = formattedWeights.subList(0, 5);
            }
        }

        return weightList;
    }

    @NotNull
    private List<Height> getHeights(List<Height> heights) {
        List<Height> heightList = new ArrayList<>();
        List<Height> formattedHeights = getChildSpecificHeights(heights);
        if (formattedHeights != null && !formattedHeights.isEmpty()) {
            if (heights.size() <= 5) {
                heightList = heights;
            } else {
                heightList = heights.subList(0, 5);
            }
        }

        return heightList;
    }

    private List<Height> getChildSpecificHeights(List<Height> heights) {
        List<Height> heightList = new ArrayList<>();
        for (Height height : heights) {
            if (height.getBaseEntityId().equals(childDetails.getCaseId())) {
                heightList.add(height);
            }
        }

        return heightList;
    }

    private List<Weight> getChildSpecificWeights(List<Weight> weights) {
        List<Weight> weightList = new ArrayList<>();
        for (Weight height : weights) {
            if (height.getBaseEntityId().equals(childDetails.getCaseId())) {
                weightList.add(height);
            }
        }

        return weightList;
    }

    private void createPTCMTVIEW(LinearLayout fragmentContainer, String labelString, String valueString) {
        TableRow tableRow = fragmentContainer.findViewById(R.id.tablerowcontainer);
        TextView label = tableRow.findViewById(R.id.label);
        TextView value = tableRow.findViewById(R.id.value);

        label.setText(labelString);
        value.setText(valueString);
    }

    public void updateVaccinationViews(List<Vaccine> vaccines, List<Alert> alertList, boolean editVaccineMode) {
        boolean showVaccine = curVaccineMode == null || !curVaccineMode.equals(editVaccineMode);
        if (fragmentContainer != null && showVaccine) {

            List<Vaccine> vaccineList = new ArrayList<>();
            if (vaccines != null && !vaccines.isEmpty()) {
                vaccineList = vaccines;
            }

            LinearLayout vaccineGroupCanvasLL = fragmentContainer.findViewById(R.id.immunizations);
            vaccineGroupCanvasLL.removeAllViews();

            CustomFontTextView title = new CustomFontTextView(getActivity());
            title.setAllCaps(true);
            title.setTextAppearance(getActivity(), android.R.style.TextAppearance_Medium);
            title.setTextColor(getResources().getColor(R.color.text_black));
            title.setText(getString(R.string.immunizations));
            vaccineGroupCanvasLL.addView(title);

            List<VaccineGroup> supportedVaccines = VaccinatorUtils.getSupportedVaccines(getActivity());
            for (VaccineGroup vaccineGroup : supportedVaccines) {

                VaccinateActionUtils.addBcg2SpecialVaccine(getActivity(), vaccineGroup, vaccineList);
                ImmunizationRowGroup curGroup = new ImmunizationRowGroup(getActivity(), editVaccineMode);
                curGroup.setData(vaccineGroup, childDetails, vaccineList, alertList);
                curGroup.setOnVaccineUndoClickListener(new ImmunizationRowGroup.OnVaccineUndoClickListener() {
                    @Override
                    public void onUndoClick(ImmunizationRowGroup vaccineGroup, VaccineWrapper vaccine) {
                        addVaccinationDialogFragment(Arrays.asList(vaccine), vaccineGroup);

                    }
                });

                vaccineGroupCanvasLL.addView(curGroup);
            }

            curVaccineMode = editVaccineMode;
        }
    }

    private void addVaccinationDialogFragment(List<VaccineWrapper> vaccineWrappers, ImmunizationRowGroup vaccineGroup) {
        FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
        Fragment prev = getActivity().getSupportFragmentManager().findFragmentByTag(DIALOG_TAG);
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);

        Date dob = getBirthDate();
        if (dob == null) {
            dob = Calendar.getInstance().getTime();
        }

        List<Vaccine> vaccineList =
                ImmunizationLibrary.getInstance().vaccineRepository().findByEntityId(childDetails.entityId());
        if (vaccineList == null) {
            vaccineList = new ArrayList<>();
        }

        VaccinationEditDialogFragment vaccinationDialogFragment = VaccinationEditDialogFragment
                .newInstance(getActivity(), dob, vaccineList, vaccineWrappers, vaccineGroup, true);
        vaccinationDialogFragment.show(ft, DIALOG_TAG);
    }

    private Date getBirthDate() {
        String birthDate = Utils.getValue(childDetails.getColumnmaps(), Constants.KEY.DOB, false);
        return Utils.dobStringToDate(birthDate);
    }

    public void updateServiceViews(Map<String, List<ServiceType>> serviceTypeMap, List<ServiceRecord> services,
                                   List<Alert> alertList, boolean editServiceMode) {
        boolean showService = curServiceMode == null || !curServiceMode.equals(editServiceMode);
        if (fragmentContainer != null && showService) {

            List<ServiceRecord> serviceRecords = new ArrayList<>();
            if (services != null && !services.isEmpty()) {
                serviceRecords = services;
            }

            LinearLayout serviceGroupCanvasLL = fragmentContainer.findViewById(R.id.services);
            serviceGroupCanvasLL.removeAllViews();

            CustomFontTextView title = new CustomFontTextView(getActivity());
            title.setAllCaps(true);
            title.setTextAppearance(getActivity(), android.R.style.TextAppearance_Medium);
            title.setTextColor(getResources().getColor(R.color.text_black));
            title.setText(getString(R.string.recurring));
            serviceGroupCanvasLL.addView(title);

            try {
                for (String type : serviceTypeMap.keySet()) {
                    ServiceRowGroup curGroup = new ServiceRowGroup(getActivity(), editServiceMode);
                    curGroup.setData(childDetails, serviceTypeMap.get(type), serviceRecords, alertList);
                    curGroup.setOnServiceUndoClickListener(new ServiceRowGroup.OnServiceUndoClickListener() {
                        @Override
                        public void onUndoClick(ServiceRowGroup serviceRowGroup, ServiceWrapper service) {
                            addServiceDialogFragment(service, serviceRowGroup);
                        }
                    });

                    serviceGroupCanvasLL.addView(curGroup);
                }
            } catch (Exception e) {
                Log.e(getClass().getName(), Log.getStackTraceString(e));
            }

            curServiceMode = editServiceMode;
        }
    }

    private void addServiceDialogFragment(ServiceWrapper serviceWrapper, ServiceRowGroup serviceRowGroup) {
        FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
        Fragment prev = getActivity().getSupportFragmentManager().findFragmentByTag(DIALOG_TAG);
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);

        String dobString = Utils.getValue(detailsMap, Constants.KEY.DOB, false);
        DateTime dateTime = Utils.dobStringToDateTime(dobString);
        if (dateTime == null) {
            dateTime = DateTime.now();
        }

        List<ServiceRecord> serviceRecordList =
                ImmunizationLibrary.getInstance().recurringServiceRecordRepository().findByEntityId(childDetails.entityId());
        if (serviceRecordList == null) {
            serviceRecordList = new ArrayList<>();
        }

        ServiceEditDialogFragment serviceEditDialogFragment =
                ServiceEditDialogFragment.newInstance(dateTime, serviceRecordList, serviceWrapper, serviceRowGroup, true);
        serviceEditDialogFragment.show(ft, DIALOG_TAG);
    }

    public void showGrowthMonitoringDialog(int growthRecordPosition) {
        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        Fragment prev = getActivity().getSupportFragmentManager().findFragmentByTag(DIALOG_TAG);
        if (prev != null) {
            fragmentTransaction.remove(prev);
        }
        fragmentTransaction.addToBackStack(null);

        String childName = constructChildName();
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
            duration = DateUtil.getDuration(dateTime);
            dob = dateTime.toDate();
        }

        if (dob == null) {
            dob = Calendar.getInstance().getTime();
        }

        Photo photo = getProfilePhotoByClient();

        WeightWrapper weightWrapper = getWeightWrapper(growthRecordPosition, childName, gender, openSrpId, duration, photo);
        HeightWrapper heightWrapper = getHeightWrapper(growthRecordPosition, childName, gender, openSrpId, duration, photo);

        EditGrowthDialogFragment editWeightDialogFragment =
                EditGrowthDialogFragment.newInstance(getActivity(), dob, weightWrapper, heightWrapper);
        editWeightDialogFragment.show(fragmentTransaction, DIALOG_TAG);

    }

    @NotNull
    private WeightWrapper getWeightWrapper(int weightPosition, String childName, String gender, String openSrpId,
                                           String duration, Photo photo) {
        WeightWrapper weightWrapper = new WeightWrapper();
        weightWrapper.setId(childDetails.entityId());

        List<Weight> weightList =
                GrowthMonitoringLibrary.getInstance().weightRepository().findByEntityId(childDetails.entityId());

        Weight weight = getWeight(weightList, weightPosition);

        if (!weightList.isEmpty()) {
            weightWrapper.setWeight(weight.getKg());
            weightWrapper.setUpdatedWeightDate(new DateTime(weight.getDate()), false);
            weightWrapper.setDbKey(weight.getId());
        }

        weightWrapper.setGender(gender);
        weightWrapper.setPatientName(childName);
        weightWrapper.setPatientNumber(openSrpId);
        weightWrapper.setPatientAge(duration);
        weightWrapper.setPhoto(photo);
        weightWrapper.setPmtctStatus(
                Utils.getValue(childDetails.getColumnmaps(), BaseChildDetailTabbedActivity.PMTCT_STATUS_LOWER_CASE, false));
        return weightWrapper;
    }

    private Weight getWeight(List<Weight> weights, int weightPosition) {
        Weight displayWeight = new Weight();
        for (Weight weight : weights) {
            if (weight.getId().equals((long) weightPosition)) {
                displayWeight = weight;
            }
        }

        return displayWeight;
    }

    @NotNull
    private HeightWrapper getHeightWrapper(int heightPosition, String childName, String gender, String openSrpId,
                                           String duration, Photo photo) {
        HeightWrapper heightWrapper = new HeightWrapper();
        heightWrapper.setId(childDetails.entityId());

        List<Height> heightList =
                GrowthMonitoringLibrary.getInstance().heightRepository().findByEntityId(childDetails.entityId());
        Height height = getHeight(heightList, heightPosition);

        if (!heightList.isEmpty()) {
            heightWrapper.setHeight(height.getCm());
            heightWrapper.setUpdatedHeightDate(new DateTime(height.getDate()), false);
            heightWrapper.setDbKey(height.getId());
        }

        heightWrapper.setGender(gender);
        heightWrapper.setPatientName(childName);
        heightWrapper.setPatientNumber(openSrpId);
        heightWrapper.setPatientAge(duration);
        heightWrapper.setPhoto(photo);
        heightWrapper.setPmtctStatus(
                Utils.getValue(childDetails.getColumnmaps(), BaseChildDetailTabbedActivity.PMTCT_STATUS_LOWER_CASE, false));
        return heightWrapper;
    }

    private Height getHeight(List<Height> heights, int heightPosition) {
        Height displayHeight = new Height();
        for (Height height : heights) {
            if (height.getId().equals((long) heightPosition)) {
                displayHeight = height;
            }
        }

        return displayHeight;
    }

    protected Photo getProfilePhotoByClient() {
        return ImageUtils.profilePhotoByClient(childDetails);
    }

    private String constructChildName() {
        String firstName = Utils.getValue(detailsMap, Constants.KEY.FIRST_NAME, true);
        String lastName = Utils.getValue(detailsMap, Constants.KEY.LAST_NAME, true);
        return Utils.getName(firstName, lastName).trim();
    }

}
