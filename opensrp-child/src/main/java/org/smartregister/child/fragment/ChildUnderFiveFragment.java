package org.smartregister.child.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.StringRes;
import androidx.core.util.Pair;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import org.apache.commons.lang3.tuple.Triple;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.joda.time.DateTime;
import org.smartregister.child.R;
import org.smartregister.child.activity.BaseChildDetailTabbedActivity;
import org.smartregister.child.contract.ChildUnderFiveFragmentContract;
import org.smartregister.child.contract.IChildDetails;
import org.smartregister.child.domain.ExtraVaccineUpdateEvent;
import org.smartregister.child.domain.WrapperParam;
import org.smartregister.child.event.DynamicVaccineType;
import org.smartregister.child.presenter.ChildUnderFiveFragmentPresenter;
import org.smartregister.child.util.Constants;
import org.smartregister.child.util.Utils;
import org.smartregister.child.view.WidgetFactory;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.domain.Alert;
import org.smartregister.growthmonitoring.GrowthMonitoringLibrary;
import org.smartregister.growthmonitoring.domain.Height;
import org.smartregister.growthmonitoring.domain.HeightWrapper;
import org.smartregister.growthmonitoring.domain.Weight;
import org.smartregister.growthmonitoring.domain.WeightWrapper;
import org.smartregister.growthmonitoring.fragment.EditGrowthDialogFragment;
import org.smartregister.growthmonitoring.util.AppProperties;
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
import org.smartregister.immunization.util.VaccinateActionUtils;
import org.smartregister.immunization.util.VaccinatorUtils;
import org.smartregister.immunization.view.ImmunizationRowGroup;
import org.smartregister.immunization.view.ServiceRowGroup;
import org.smartregister.util.DateUtil;
import org.smartregister.view.customcontrols.CustomFontTextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import timber.log.Timber;

import static org.smartregister.child.util.Utils.createGroupNameTextView;
import static org.smartregister.immunization.util.VaccinatorUtils.dpToPx;
import static org.smartregister.util.Utils.convertDateFormat;

/**
 * Created by ndegwamartin on 06/03/2019.
 */
public class ChildUnderFiveFragment extends Fragment {
    private static final String DIALOG_TAG = "ChildImmunoActivity_DIALOG_TAG";
    private static Boolean monitorGrowth = false;
    private CommonPersonObjectClient childDetails;
    private Map<String, String> detailsMap;
    private LinearLayout fragmentContainer;
    private View heightWidgetLayout;
    private Boolean curVaccineMode;
    private Boolean curServiceMode;
    private LinearLayout serviceGroupCanvasLL;
    private LinearLayout extraVaccinesLayout;
    private LinearLayout boosterImmunizationsLayout;
    private boolean showRecurringServices = true;
    private List<Triple<String, String, String>> extraVaccines;
    private List<Triple<String, String, String>> boosterImmunizations;
    private final ChildUnderFiveFragmentContract.Presenter presenter;

    public ChildUnderFiveFragment() {
        presenter = new ChildUnderFiveFragmentPresenter();
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
        monitorGrowth = GrowthMonitoringLibrary.getInstance().getAppProperties().hasProperty(AppProperties.KEY.MONITOR_GROWTH) && GrowthMonitoringLibrary.getInstance().getAppProperties().getPropertyBoolean(AppProperties.KEY.MONITOR_GROWTH);

    }

    @Override
    public void onStart() {
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
        super.onStart();
    }

    @Override
    public void onPause() {
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
        super.onPause();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        if (getActivity() instanceof IChildDetails) {
            childDetails = ((IChildDetails) getActivity()).getChildDetails();
            detailsMap = childDetails.getColumnmaps();
        }

        View underFiveFragment = inflater.inflate(R.layout.child_under_five_fragment, container, false);
        fragmentContainer = underFiveFragment.findViewById(R.id.container);
        serviceGroupCanvasLL = fragmentContainer.findViewById(R.id.services);
        extraVaccinesLayout = fragmentContainer.findViewById(R.id.extra_vaccines);
        boosterImmunizationsLayout = fragmentContainer.findViewById(R.id.booster_immunizations);

        if (monitorGrowth) {
            heightWidgetLayout = underFiveFragment.findViewById(R.id.height_widget_layout);
            heightWidgetLayout.setVisibility(View.VISIBLE);
        }

        Utils.refreshDataCaptureStrategyBanner(requireActivity(), ((BaseChildDetailTabbedActivity) requireActivity()).getOpenSRPContext().allSharedPreferences().fetchCurrentLocality());

        return underFiveFragment;
    }

    public void setDetailsMap(Map<String, String> detailsMap) {
        this.detailsMap = detailsMap;
        if (getActivity() instanceof IChildDetails) {
            ((IChildDetails) getActivity()).setChildDetails(this.detailsMap);
        }
    }


    public void showRecurringServices(boolean showRecurringServices) {
        this.showRecurringServices = showRecurringServices;
    }

    public void loadGrowthMonitoringView(List<Weight> weightList, List<Height> heightList, boolean editGrowthMonitoringMode) {

        if (fragmentContainer != null) {
            createGrowthLayout(weightList, heightList, fragmentContainer, editGrowthMonitoringMode);
        }
    }

    private void createGrowthLayout(List<Weight> weights, List<Height> heights, LinearLayout fragmentContainer, boolean editMode) {
        WidgetFactory widgetFactory = new WidgetFactory();
        ArrayList<View.OnClickListener> listeners = new ArrayList<>();

        ArrayList<Boolean> weightEditMode = new ArrayList<>();
        List<Weight> weightList = presenter.getWeights(childDetails.getCaseId(), weights);

        // Sort the weights
        presenter.sortTheWeightsInDescendingOrder(weightList);

        LinkedHashMap<Long, Pair<String, String>> weightMap = updateWeightMap(editMode, weightEditMode, listeners, weightList);

        LayoutInflater layoutInflater = requireActivity().getLayoutInflater();

        if (weightMap.size() > 0) {
            widgetFactory.createWeightWidget(layoutInflater, fragmentContainer, weightMap, listeners, weightEditMode);
        }

        if (monitorGrowth) {
            ArrayList<Boolean> heightEditMode = new ArrayList<>();
            List<Height> heightList = presenter.getHeights(childDetails.getCaseId(), heights);

            // Sort the heights
            presenter.sortTheHeightsInDescendingOrder(heightList);
            ArrayList<View.OnClickListener> heightListeners = new ArrayList<>();

            LinkedHashMap<Long, Pair<String, String>> heightMap =
                    updateHeightMap(editMode, heightEditMode, heightListeners, heightList);
            if (heightMap.size() > 0) {
                widgetFactory.createHeightWidget(layoutInflater, fragmentContainer, heightMap, heightListeners, heightEditMode);
            }
        }

        ((NestedScrollView) ((View) fragmentContainer.getParent()).findViewById(R.id.scrollView)).smoothScrollTo(0, 0);
    }

    private LinkedHashMap<Long, Pair<String, String>> updateWeightMap(boolean editMode, ArrayList<Boolean> weightEditMode, ArrayList<View.OnClickListener> listeners, List<Weight> weightList) {
        LinkedHashMap<Long, Pair<String, String>> weightMap = new LinkedHashMap<>();
        for (int i = 0; i < weightList.size(); i++) {
            Weight weight = weightList.get(i);
            String formattedAge = "";
            if (weight.getDate() != null) {
                Date weightDate = weight.getDate();
                String birthDate = Utils.getValue(detailsMap, Constants.KEY.DOB, false);
                Date birth = Utils.dobStringToDate(birthDate);
                if (birth != null) {
                    long timeDiff = Math.abs(weightDate.getTime() - birth.getTime());
                    Timber.v("%s", timeDiff);
                    if (timeDiff >= 0) {
                        formattedAge = DateUtil.getDuration(timeDiff, Locale.ENGLISH);
                        Timber.v(formattedAge);
                    }
                }
            }

            weightMap.put(weight.getId() - 1, Pair.create(formattedAge, Utils.kgStringSuffix(Utils.formatNumber(String.valueOf(weight.getKg())))));

            boolean lessThanThreeMonthsEventCreated = WeightUtils.lessThanThreeMonths(weight);
            weightEditMode.add(lessThanThreeMonthsEventCreated && editMode && !formattedAge.startsWith("0"));

            final long weightTaken = weight.getDate().getTime();
            View.OnClickListener onClickListener = v -> showGrowthMonitoringDialog(weightTaken);
            listeners.add(onClickListener);
        }


        return weightMap;
    }

    private LinkedHashMap<Long, Pair<String, String>> updateHeightMap(boolean editMode, ArrayList<Boolean> heightEditMode, ArrayList<View.OnClickListener> listeners, List<Height> heightList) {
        LinkedHashMap<Long, Pair<String, String>> heightMap = new LinkedHashMap<>();
        for (int i = 0; i < heightList.size(); i++) {
            Height height = heightList.get(i);
            String formattedAge = "";
            if (height.getDate() != null) {
                Date heightDate = height.getDate();
                Date birth = presenter.getBirthDate(childDetails.getColumnmaps());
                if (birth != null) {
                    long timeDiff = Math.abs(heightDate.getTime() - birth.getTime());
                    Timber.v("%s", timeDiff);
                    if (timeDiff >= 0) {
                        formattedAge = DateUtil.getDuration(timeDiff, Locale.ENGLISH);
                        Timber.v(formattedAge);
                    }
                }
            }

            heightMap.put(height.getId() - 1, Pair.create(formattedAge, Utils.cmStringSuffix(height.getCm())));

            boolean lessThanThreeMonthsEventCreated = HeightUtils.lessThanThreeMonths(height);
            heightEditMode.add(lessThanThreeMonthsEventCreated && editMode && !formattedAge.startsWith("0"));

            final long heightTaken = height.getDate().getTime();
            View.OnClickListener onClickListener = v -> showGrowthMonitoringDialog(heightTaken);
            listeners.add(onClickListener);
        }

        return heightMap;
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
            vaccineGroupCanvasLL.addView(getSectionTitle(R.color.text_black, R.string.immunizations));

            boolean addedBcg2Vaccine = false;
            List<VaccineGroup> supportedVaccines = VaccinatorUtils.getSupportedVaccines(getActivity());
            for (VaccineGroup vaccineGroup : supportedVaccines) {

                if (!addedBcg2Vaccine) {
                    addedBcg2Vaccine = VaccinateActionUtils.addBcg2SpecialVaccine(getActivity(), vaccineGroup, vaccineList);
                }

                ImmunizationRowGroup curGroup = new ImmunizationRowGroup(requireActivity(), editVaccineMode);
                curGroup.setData(vaccineGroup, childDetails, vaccineList, alertList);
                curGroup.setOnVaccineUndoClickListener((vaccineGroup1, vaccine) -> addVaccinationDialogFragment(Collections.singletonList(vaccine), vaccineGroup1));

                TextView groupNameTextView = createGroupNameTextView(requireActivity(), vaccineGroup.name);
                vaccineGroupCanvasLL.addView(groupNameTextView);
                vaccineGroupCanvasLL.addView(curGroup);
            }

            curVaccineMode = editVaccineMode;
        }
    }

    private void addVaccinationDialogFragment(List<VaccineWrapper> vaccineWrappers, ImmunizationRowGroup vaccineGroup) {
        if (getActivity() != null) {
            FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
            Fragment prev = getActivity().getSupportFragmentManager().findFragmentByTag(DIALOG_TAG);
            if (prev != null) {
                ft.remove(prev);
            }
            ft.addToBackStack(null);

            Date dob = presenter.getBirthDate(childDetails.getColumnmaps());
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
    }

    public void updateServiceViews(Map<String, List<ServiceType>> serviceTypeMap, List<ServiceRecord> services, List<Alert> alertList, boolean editServiceMode) {
        boolean showService = curServiceMode == null || !curServiceMode.equals(editServiceMode);
        if (fragmentContainer != null && showService) {

            List<ServiceRecord> serviceRecords = new ArrayList<>();
            if (services != null && !services.isEmpty()) {
                serviceRecords = services;
            }

            serviceGroupCanvasLL.removeAllViews();
            serviceGroupCanvasLL.addView(getSectionTitle(R.color.black, R.string.recurring));

            try {
                for (String type : serviceTypeMap.keySet()) {
                    ServiceRowGroup curGroup = new ServiceRowGroup(getActivity(), editServiceMode);
                    curGroup.setData(childDetails, serviceTypeMap.get(type), serviceRecords, alertList);
                    curGroup.setOnServiceUndoClickListener((serviceRowGroup, service) -> addServiceDialogFragment(service, serviceRowGroup));

                    TextView groupNameTextView = createGroupNameTextView(getActivity(), type);
                    serviceGroupCanvasLL.addView(groupNameTextView);
                    serviceGroupCanvasLL.addView(curGroup);
                }
            } catch (Exception e) {
                Timber.e(Log.getStackTraceString(e));
            }

            curServiceMode = editServiceMode;
        }
    }

    public void updateExtraVaccinesView(DynamicVaccineType vaccineType, boolean editable) {
        if (vaccineType == DynamicVaccineType.PRIVATE_SECTOR_VACCINE) {
            createExtraVaccinesViews(getExtraVaccines(), extraVaccinesLayout, R.string.extra_vaccines, editable);
        }
        if (vaccineType == DynamicVaccineType.BOOSTER_IMMUNIZATIONS) {
            createExtraVaccinesViews(getBoosterImmunizations(), boosterImmunizationsLayout, R.string.booster_immunizations, editable);
        }
    }

    private void createExtraVaccinesViews(List<Triple<String, String, String>> vaccinesList,
                                          LinearLayout vaccineLayout, @StringRes int titleResource,
                                          boolean editable) {

        if (vaccineLayout != null && vaccinesList != null && !vaccinesList.isEmpty()) {
            vaccineLayout.setVisibility(View.VISIBLE);
            vaccineLayout.removeAllViews();
            vaccineLayout.addView(getSectionTitle(R.color.black, titleResource));
            for (Triple<String, String, String> vaccine : vaccinesList) {
                RelativeLayout immunizationRow = (RelativeLayout) requireActivity().getLayoutInflater().inflate(R.layout.view_immunization_row_card, null);
                immunizationRow.setPadding(immunizationRow.getPaddingLeft(), immunizationRow.getPaddingTop(), immunizationRow.getPaddingRight(), immunizationRow.getPaddingBottom() + 10);

                TextView vaccineTextView = immunizationRow.findViewById(R.id.name_tv);
                vaccineTextView.setMaxWidth(dpToPx(requireActivity(), 240f));
                String vaccineName = vaccine.getMiddle();
                vaccineTextView.setText(vaccineName);

                Button statusButton = immunizationRow.findViewById(R.id.status_iv);
                statusButton.setBackgroundResource(org.smartregister.immunization.R.drawable.vaccine_card_background_green);
                statusButton.setVisibility(View.VISIBLE);

                TextView dateTextView = immunizationRow.findViewById(R.id.status_text_tv);
                String serviceDate = convertDateFormat(vaccine.getRight(), true);
                dateTextView.setText(serviceDate);

                String baseEntityId = vaccine.getLeft();

                if (editable) {
                    TextView editButton = immunizationRow.findViewById(R.id.undo_b);
                    editButton.setVisibility(View.VISIBLE);
                    editButton.setTag(R.id.key, immunizationRow);
                    editButton.setOnClickListener(view -> {

                        ExtraVaccineEditDialogFragment extraVaccineFragment = ExtraVaccineEditDialogFragment.newInstance();

                        Bundle arguments = new Bundle();
                        updateChildDetails(Constants.KEY.SERVICE_DATE, serviceDate);
                        updateChildDetails(Constants.KEY.VACCINE, vaccineName);
                        updateChildDetails(Constants.KEY.BASE_ENTITY_ID, baseEntityId);

                        arguments.putSerializable(Constants.KEY.DETAILS, childDetails);
                        arguments.putString(Constants.KEY.BASE_ENTITY_ID, baseEntityId);
                        extraVaccineFragment.setArguments(arguments);

                        requireActivity().getSupportFragmentManager()
                                .beginTransaction()
                                .add(extraVaccineFragment, ExtraVaccineEditDialogFragment.TAG)
                                .commitNow();
                    });
                }

                vaccineLayout.addView(immunizationRow);
            }
        }
    }

    private void updateChildDetails(String key, String value) {
        childDetails.getColumnmaps().put(key, value);
        childDetails.getDetails().put(key, value);
    }

    private CustomFontTextView getSectionTitle(int textColorResource, int textResource) {
        CustomFontTextView title = new CustomFontTextView(getActivity());
        title.setAllCaps(true);
        title.setTextAppearance(getActivity(), android.R.style.TextAppearance_Medium);
        title.setTextColor(getResources().getColor(textColorResource));
        title.setText(getString(textResource));
        return title;
    }

    private void addServiceDialogFragment(ServiceWrapper serviceWrapper, ServiceRowGroup serviceRowGroup) {
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        Fragment fragmentByTag = getActivity().getSupportFragmentManager().findFragmentByTag(DIALOG_TAG);
        if (fragmentByTag != null) {
            transaction.remove(fragmentByTag);
        }
        transaction.addToBackStack(null);

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
        serviceEditDialogFragment.show(transaction, DIALOG_TAG);
    }

    public void showGrowthMonitoringDialog(long growthRecordPosition) {
        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        Fragment prev = getActivity().getSupportFragmentManager().findFragmentByTag(DIALOG_TAG);
        if (prev != null) {
            fragmentTransaction.remove(prev);
        }
        fragmentTransaction.addToBackStack(null);

        WrapperParam wrapperParams = presenter.getWrapperParam(detailsMap, growthRecordPosition);

        WeightWrapper weightWrapper = presenter.getWeightWrapper(wrapperParams);

        HeightWrapper heightWrapper = null;

        if (GrowthMonitoringLibrary.getInstance().getAppProperties().hasProperty(AppProperties.KEY.MONITOR_GROWTH) && GrowthMonitoringLibrary.getInstance().getAppProperties().getPropertyBoolean(AppProperties.KEY.MONITOR_GROWTH)) {
            heightWrapper = presenter.getHeightWrapper(wrapperParams);
        }

        EditGrowthDialogFragment editWeightDialogFragment = EditGrowthDialogFragment.newInstance(wrapperParams.getDob(), weightWrapper, heightWrapper);
        editWeightDialogFragment.show(fragmentTransaction, DIALOG_TAG);

    }

    /**
     * Do not show the recurring services
     */
    public void hideOrShowRecurringServices() {
        if (this.showRecurringServices) {
            serviceGroupCanvasLL.setVisibility(View.VISIBLE);
        } else {
            serviceGroupCanvasLL.setVisibility(View.GONE);
        }
    }

    public void setExtraVaccines(List<Triple<String, String, String>> extraVaccines) {
        this.extraVaccines = extraVaccines;
    }

    public List<Triple<String, String, String>> getExtraVaccines() {
        return extraVaccines;
    }

    public List<Triple<String, String, String>> getBoosterImmunizations() {
        return boosterImmunizations;
    }

    public void setBoosterImmunizations(List<Triple<String, String, String>> boosterImmunizations) {
        this.boosterImmunizations = boosterImmunizations;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onVaccineUpdated(ExtraVaccineUpdateEvent extraVaccineUpdateEvent) {
        if (updateServiceDate(extraVaccineUpdateEvent)) {
            updateExtraVaccinesView(DynamicVaccineType.PRIVATE_SECTOR_VACCINE, true);
            updateExtraVaccinesView(DynamicVaccineType.BOOSTER_IMMUNIZATIONS, true);
        }
    }

    private boolean updateServiceDate(ExtraVaccineUpdateEvent extraVaccineUpdateEvent) {

        if (getExtraVaccines() != null && !getExtraVaccines().isEmpty()) {
            return findVaccine(extraVaccineUpdateEvent, getExtraVaccines());
        }

        if (getBoosterImmunizations() != null && !getBoosterImmunizations().isEmpty()) {
            return findVaccine(extraVaccineUpdateEvent, getBoosterImmunizations());
        }
        return false;
    }

    private boolean findVaccine(ExtraVaccineUpdateEvent extraVaccineUpdateEvent,
                                List<Triple<String, String, String>> vaccineTriples) {
        boolean foundVaccine = false;

        for (int index = 0; index < vaccineTriples.size(); index++) {
            Triple<String, String, String> vaccineTriple = vaccineTriples.get(index);
            String vaccineName = vaccineTriple.getMiddle();
            if (vaccineName.equalsIgnoreCase(extraVaccineUpdateEvent.getVaccine())) {
                foundVaccine = true;
                //Replace the vaccine entry
                Triple<String, String, String> removedVaccine = vaccineTriples.remove(index);
                if (!extraVaccineUpdateEvent.isRemoved()) {
                    vaccineTriples.add(index, Triple.of(removedVaccine.getLeft(), removedVaccine.getMiddle(),
                            extraVaccineUpdateEvent.getVaccineDate()));
                }
                break;
            }
        }
        return foundVaccine;
    }
}
