package org.smartregister.child.activity;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.SparseIntArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.WordUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.commons.lang3.tuple.Triple;
import org.joda.time.DateTime;
import org.opensrp.api.constants.Gender;
import org.pcollections.TreePVector;
import org.smartregister.CoreLibrary;
import org.smartregister.child.Configurable;
import org.smartregister.child.R;
import org.smartregister.child.domain.NamedObject;
import org.smartregister.child.domain.RegisterClickables;
import org.smartregister.child.toolbar.LocationSwitcherToolbar;
import org.smartregister.child.util.AsyncTaskUtils;
import org.smartregister.child.util.Constants;
import org.smartregister.child.util.DBConstants;
import org.smartregister.child.util.JsonFormUtils;
import org.smartregister.child.util.Utils;
import org.smartregister.child.view.SiblingPicturesGroup;
import org.smartregister.commonregistry.AllCommonsRepository;
import org.smartregister.commonregistry.CommonPersonObject;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.domain.Alert;
import org.smartregister.domain.Photo;
import org.smartregister.growthmonitoring.GrowthMonitoringLibrary;
import org.smartregister.growthmonitoring.domain.Weight;
import org.smartregister.growthmonitoring.domain.WeightWrapper;
import org.smartregister.growthmonitoring.fragment.GrowthDialogFragment;
import org.smartregister.growthmonitoring.fragment.RecordWeightDialogFragment;
import org.smartregister.growthmonitoring.listener.WeightActionListener;
import org.smartregister.growthmonitoring.repository.WeightRepository;
import org.smartregister.immunization.ImmunizationLibrary;
import org.smartregister.immunization.db.VaccineRepo;
import org.smartregister.immunization.domain.ServiceRecord;
import org.smartregister.immunization.domain.ServiceSchedule;
import org.smartregister.immunization.domain.ServiceType;
import org.smartregister.immunization.domain.ServiceWrapper;
import org.smartregister.immunization.domain.Vaccine;
import org.smartregister.immunization.domain.VaccineSchedule;
import org.smartregister.immunization.domain.VaccineWrapper;
import org.smartregister.immunization.fragment.ActivateChildStatusDialogFragment;
import org.smartregister.immunization.fragment.ServiceDialogFragment;
import org.smartregister.immunization.fragment.UndoServiceDialogFragment;
import org.smartregister.immunization.fragment.UndoVaccinationDialogFragment;
import org.smartregister.immunization.fragment.VaccinationDialogFragment;
import org.smartregister.immunization.listener.ServiceActionListener;
import org.smartregister.immunization.listener.VaccinationActionListener;
import org.smartregister.immunization.listener.VaccineCardAdapterLoadingListener;
import org.smartregister.immunization.repository.RecurringServiceRecordRepository;
import org.smartregister.immunization.repository.RecurringServiceTypeRepository;
import org.smartregister.immunization.repository.VaccineRepository;
import org.smartregister.immunization.util.ImageUtils;
import org.smartregister.immunization.util.RecurringServiceUtils;
import org.smartregister.immunization.util.VaccinateActionUtils;
import org.smartregister.immunization.util.VaccinatorUtils;
import org.smartregister.immunization.view.ServiceGroup;
import org.smartregister.immunization.view.VaccineGroup;
import org.smartregister.location.helper.LocationHelper;
import org.smartregister.repository.DetailsRepository;
import org.smartregister.service.AlertService;
import org.smartregister.util.DateUtil;
import org.smartregister.util.OpenSRPImageLoader;
import org.smartregister.view.activity.DrishtiApplication;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * Created by ndegwamartin on 06/03/2019.
 */
public abstract class BaseChildImmunizationActivity extends BaseActivity
        implements LocationSwitcherToolbar.OnLocationChangeListener, WeightActionListener, VaccinationActionListener, ServiceActionListener {

    private static final String TAG = BaseChildImmunizationActivity.class.getCanonicalName();
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy");
    private static final String DIALOG_TAG = "ChildImmunoActivity_DIALOG_TAG";
    private ArrayList<VaccineGroup> vaccineGroups;
    private ArrayList<ServiceGroup> serviceGroups;
    private static final ArrayList<String> COMBINED_VACCINES;
    private static final HashMap<String, String> COMBINED_VACCINES_MAP;
    private boolean bcgScarNotificationShown;
    private boolean weightNotificationShown;
    private static final int RANDOM_MAX_RANGE = 4232;
    private static final int RANDOM_MIN_RANGE = 213;
    private static final int RECORD_WEIGHT_BUTTON_ACTIVE_MIN = 12;
    private final String SHOW_BCG2_REMINDER = "show_bcg2_reminder";
    public static final String SHOW_BCG_SCAR = "show_bcg_scar";


    static {
        COMBINED_VACCINES = new ArrayList<>();
        COMBINED_VACCINES_MAP = new HashMap<>();
        COMBINED_VACCINES.add("Measles 1");
        COMBINED_VACCINES_MAP.put("Measles 1", "Measles 1 / MR 1");
        COMBINED_VACCINES.add("MR 1");
        COMBINED_VACCINES_MAP.put("MR 1", "Measles 1 / MR 1");
        COMBINED_VACCINES.add("Measles 2");
        COMBINED_VACCINES_MAP.put("Measles 2", "Measles 2 / MR 2");
        COMBINED_VACCINES.add("MR 2");
        COMBINED_VACCINES_MAP.put("MR 2", "Measles 2 / MR 2");
    }

    // Views
    private LocationSwitcherToolbar toolbar;

    // Data
    private CommonPersonObjectClient childDetails;
    private RegisterClickables registerClickables;
    private DetailsRepository detailsRepository;
    private boolean dialogOpen = false;
    private boolean isChildActive = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        detailsRepository = getOpenSRPContext().detailsRepository();

        toolbar = (LocationSwitcherToolbar) getToolbar();
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToRegisterPage();
            }
        });
        toolbar.setOnLocationChangeListener(this);
//       View view= toolbar.findViewById(R.id.immunization_separator);
//        view.setBackground(R.drawable.vertical_seperator_female);

        // Get child details from bundled data
        Bundle extras = this.getIntent().getExtras();
        if (extras != null) {
            Serializable serializable = extras.getSerializable(Constants.INTENT_KEY.EXTRA_CHILD_DETAILS);
            if (serializable != null && serializable instanceof CommonPersonObjectClient) {
                childDetails = (CommonPersonObjectClient) serializable;
            }

            serializable = extras.getSerializable(Constants.INTENT_KEY.EXTRA_REGISTER_CLICKABLES);
            if (serializable != null && serializable instanceof RegisterClickables) {
                registerClickables = (RegisterClickables) serializable;
            }
        }

        bcgScarNotificationShown = false;
        weightNotificationShown = false;

        toolbar.init(this);
        setLastModified(false);
    }

    protected abstract Activity getActivity();

    protected abstract void goToRegisterPage();

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(Constants.INTENT_KEY.EXTRA_CHILD_DETAILS, childDetails);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        Serializable serializable = savedInstanceState.getSerializable(Constants.INTENT_KEY.EXTRA_CHILD_DETAILS);
        if (serializable != null && serializable instanceof CommonPersonObjectClient) {
            childDetails = (CommonPersonObjectClient) serializable;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (vaccineGroups != null) {
            LinearLayout vaccineGroupCanvasLL = findViewById(R.id.vaccine_group_canvas_ll);
            vaccineGroupCanvasLL.removeAllViews();
            vaccineGroups = null;
        }

        if (serviceGroups != null) {
            LinearLayout serviceGroupCanvasLL = findViewById(R.id.service_group_canvas_ll);
            serviceGroupCanvasLL.removeAllViews();
            serviceGroups = null;
        }
        updateViews();

    }

    private boolean isDataOk() {
        return childDetails != null && childDetails.getDetails() != null;
    }

    private void updateViews() {
        findViewById(R.id.profile_name_layout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchDetailActivity(getActivity(), childDetails, null);
            }
        });

        // TODO: update all views using child data
        Map<String, String> details = detailsRepository.getAllDetailsForClient(childDetails.entityId());

        Utils.putAll(childDetails.getColumnmaps(), details);
        isChildActive = isActiveStatus(childDetails);

        showChildsStatus(childDetails);

        updateGenderViews();
        toolbar.setTitle(updateActivityTitle());
        updateAgeViews();
        updateChildIdViews();


        AlertService alertService = getOpenSRPContext().alertService();


        UpdateViewTask updateViewTask = new UpdateViewTask();
        updateViewTask.setWeightRepository(GrowthMonitoringLibrary.getInstance().weightRepository());
        updateViewTask.setVaccineRepository(ImmunizationLibrary.getInstance().vaccineRepository());
        updateViewTask.setRecurringServiceTypeRepository(ImmunizationLibrary.getInstance().recurringServiceTypeRepository());
        updateViewTask.setRecurringServiceRecordRepository(ImmunizationLibrary.getInstance().recurringServiceRecordRepository());
        updateViewTask.setAlertService(alertService);
        Utils.startAsyncTask(updateViewTask, null);
    }

    private void updateProfilePicture(Gender gender) {
        if (isDataOk()) {
            ImageView profileImageIV = findViewById(R.id.profile_image_iv);

            if (childDetails.entityId() != null) { //image already in local storage most likey ):
                //set profile image by passing the client id.If the image doesn't exist in the image repository then download and save locally
                profileImageIV.setTag(R.id.entity_id, childDetails.entityId());
                DrishtiApplication.getCachedImageLoaderInstance().getImageByClientId(childDetails.entityId(), OpenSRPImageLoader.getStaticImageListener(profileImageIV, ImageUtils.profileImageResourceByGender(gender), ImageUtils.profileImageResourceByGender(gender)));

            }
        }
    }

    private void updateChildIdViews() {
        String name = "";
        String childId = "";
        if (isDataOk()) {
            name = constructChildName();
            childId = Utils.getValue(childDetails.getColumnmaps(), DBConstants.KEY.ZEIR_ID, false);
        }

        TextView nameTV = findViewById(R.id.name_tv);
        nameTV.setText(name);
        TextView childIdTV = findViewById(R.id.child_id_tv);
        childIdTV.setText(String.format("%s: %s", getString(R.string.label_zeir), childId));

        Utils.startAsyncTask(new GetSiblingsTask(), null);
    }

    private void updateAgeViews() {
        String dobString = "";
        String formattedAge = "";
        String formattedDob = "";
        if (isDataOk()) {
            dobString = Utils.getValue(childDetails.getColumnmaps(), Constants.EC_CHILD_TABLE.DOB, false);
            Date dob = Utils.dobStringToDate(dobString);
            if (dob != null) {
                formattedDob = DATE_FORMAT.format(dob);
                long timeDiff = Calendar.getInstance().getTimeInMillis() - dob.getTime();

                if (timeDiff >= 0) {
                    formattedAge = DateUtil.getDuration(timeDiff);
                }
            }
        }
        TextView dobTV = findViewById(R.id.dob_tv);
        dobTV.setText(String.format("%s: %s", getString(R.string.birthdate), formattedDob));
        TextView ageTV = findViewById(R.id.age_tv);
        ageTV.setText(String.format("%s: %s", getString(R.string.age), formattedAge));
    }

    private void updateGenderViews() {
        Gender gender = Gender.UNKNOWN;
        if (isDataOk()) {
            String genderString = Utils.getValue(childDetails, DBConstants.KEY.GENDER, false);
            if (genderString != null && genderString.equalsIgnoreCase(Constants.GENDER.FEMALE)) {
                gender = Gender.FEMALE;
            } else if (genderString != null && genderString.equalsIgnoreCase(Constants.GENDER.MALE)) {
                gender = Gender.MALE;
            }
        }
        updateGenderViews(gender);
    }

    @Override
    protected int[] updateGenderViews(Gender gender) {
        int[] selectedColor = super.updateGenderViews(gender);

        String identifier = getString(R.string.neutral_sex_id);
        int toolbarResource = R.drawable.vertical_separator_neutral;
        if (gender.equals(Gender.FEMALE)) {
            toolbarResource = R.drawable.vertical_separator_female;
            identifier = getString(R.string.female_sex_id);
        } else if (gender.equals(Gender.MALE)) {
            toolbarResource = R.drawable.vertical_separator_male;
            identifier = getString(R.string.male_sex_id);
        }
        toolbar.updateSeparatorView(toolbarResource);

        TextView childSiblingsTV = findViewById(R.id.child_siblings_tv);
        childSiblingsTV.setText(
                String.format(getString(R.string.child_siblings), "").toUpperCase());
        updateProfilePicture(gender);

        return selectedColor;
    }

    private void updateServiceViews(Map<String, List<ServiceType>> serviceTypeMap, List<ServiceRecord> serviceRecordList, List<Alert> alerts) {
        Map<String, List<ServiceType>> foundServiceTypeMap = new LinkedHashMap<>();
        if (serviceGroups == null) {
            for (String type : serviceTypeMap.keySet()) {
                if (foundServiceTypeMap.containsKey(type)) {
                    continue;
                }

                for (ServiceRecord serviceRecord : serviceRecordList) {
                    if (serviceRecord.getSyncStatus().equals(RecurringServiceTypeRepository.TYPE_Unsynced) && serviceRecord.getType().equals(type)) {
                        foundServiceTypeMap.put(type, serviceTypeMap.get(type));
                        break;
                    }
                }

                if (foundServiceTypeMap.containsKey(type)) {
                    continue;
                }

                for (Alert a : alerts) {
                    if (StringUtils.containsIgnoreCase(a.scheduleName(), type)
                            || StringUtils.containsIgnoreCase(a.visitCode(), type)) {
                        foundServiceTypeMap.put(type, serviceTypeMap.get(type));
                        break;
                    }
                }

            }

            if (foundServiceTypeMap.isEmpty()) {
                return;
            }


            serviceGroups = new ArrayList<>();
            LinearLayout serviceGroupCanvasLL = findViewById(R.id.service_group_canvas_ll);

            ServiceGroup curGroup = new ServiceGroup(this);
            curGroup.setChildActive(isChildActive);
            curGroup.setData(childDetails, foundServiceTypeMap, serviceRecordList, alerts);
            curGroup.setOnServiceClickedListener(new ServiceGroup.OnServiceClickedListener() {
                @Override
                public void onClick(ServiceGroup serviceGroup, ServiceWrapper
                        serviceWrapper) {
                    if (dialogOpen) {
                        return;
                    }

                    dialogOpen = true;
                    if (isChildActive) {
                        addServiceDialogFragment(serviceWrapper, serviceGroup);
                    } else {
                        showActivateChildStatusDialogBox();
                    }
                }
            });
            curGroup.setOnServiceUndoClickListener(new ServiceGroup.OnServiceUndoClickListener() {
                @Override
                public void onUndoClick(ServiceGroup serviceGroup, ServiceWrapper serviceWrapper) {
                    if (dialogOpen) {
                        return;
                    }

                    dialogOpen = true;
                    if (isChildActive) {
                        addServiceUndoDialogFragment(serviceGroup, serviceWrapper);
                    } else {
                        showActivateChildStatusDialogBox();
                    }
                }
            });
            serviceGroupCanvasLL.addView(curGroup);
            serviceGroups.add(curGroup);
        } else {
            for (ServiceGroup serviceGroup : serviceGroups) {
                try {
                    serviceGroup.setChildActive(isChildActive);
                    serviceGroup.updateChildsActiveStatus();
                } catch (Exception e) {
                    Log.e(TAG, e.getMessage(), e);
                }
            }
        }

    }

    private void updateVaccinationViews(List<Vaccine> vaccineList, List<Alert> alerts) {

        if (vaccineGroups == null) {

            final String BCG_NAME = "BCG";
            final String BCG2_NAME = "BCG 2";
            final String BCG_NO_SCAR_NAME = "BCG: no scar";
            final String BCG_SCAR_NAME = "BCG: scar";
//            final String VACCINE_GROUP_BIRTH_NAME = "Birth";
            final int BIRTH_VACCINE_GROUP_INDEX = 0;
            List<org.smartregister.immunization.domain.jsonmapping.VaccineGroup> compiledVaccineGroups;

            vaccineGroups = new ArrayList<>();
            List<org.smartregister.immunization.domain.jsonmapping.VaccineGroup> supportedVaccines = VaccinatorUtils.getSupportedVaccines(this);

            boolean showBcg2Reminder = ((childDetails.getColumnmaps().containsKey(SHOW_BCG2_REMINDER)) && Boolean.parseBoolean(childDetails.getColumnmaps().get(SHOW_BCG2_REMINDER)));
            boolean showBcgScar = (childDetails.getColumnmaps().containsKey(SHOW_BCG_SCAR));

            org.smartregister.immunization.domain.jsonmapping.VaccineGroup birthVaccineGroup = (org.smartregister.immunization.domain.jsonmapping.VaccineGroup)
                    clone(getVaccineGroupByName(supportedVaccines, getString(R.string.vaccine_group_birth_name)));

            if (showBcg2Reminder) {

                compiledVaccineGroups = TreePVector.from(supportedVaccines).minus(BIRTH_VACCINE_GROUP_INDEX).plus(BIRTH_VACCINE_GROUP_INDEX, birthVaccineGroup);

                updateVaccineName(getVaccineByName(birthVaccineGroup.vaccines, BCG_NAME), BCG_NO_SCAR_NAME);

                List<org.smartregister.immunization.domain.jsonmapping.Vaccine> specialVaccines = getJsonVaccineGroup("special_vaccines.json");
                if (specialVaccines != null && !specialVaccines.isEmpty()) {
                    for (org.smartregister.immunization.domain.jsonmapping.Vaccine vaccine : specialVaccines) {
                        if (vaccine.name.contains(BCG_NAME) && BCG_NAME.equals(vaccine.type)) {
                            vaccine.name = BCG2_NAME;
                            birthVaccineGroup.vaccines.add(vaccine);
                            break;
                        }
                    }

                }
            } else if (showBcgScar) {

                compiledVaccineGroups = TreePVector.from(supportedVaccines).minus(BIRTH_VACCINE_GROUP_INDEX).plus(BIRTH_VACCINE_GROUP_INDEX, birthVaccineGroup);

                final long DATE = Long.valueOf(childDetails.getColumnmaps().get(SHOW_BCG_SCAR));

                List<org.smartregister.immunization.domain.jsonmapping.Vaccine> specialVaccines = getJsonVaccineGroup("special_vaccines.json");
                if (specialVaccines != null && !specialVaccines.isEmpty()) {
                    for (org.smartregister.immunization.domain.jsonmapping.Vaccine vaccine : specialVaccines) {
                        if (vaccine.name.contains(BCG_NAME) && BCG_NAME.equals(vaccine.type)) {
                            vaccine.name = BCG_SCAR_NAME;
                            birthVaccineGroup.vaccines.add(vaccine);
                            vaccineList.add(createDummyVaccine(BCG_SCAR_NAME, new Date(DATE), VaccineRepository.TYPE_Synced));
                            break;
                        }
                    }

                }
            } else {
                compiledVaccineGroups = supportedVaccines;
            }

            for (org.smartregister.immunization.domain.jsonmapping.VaccineGroup vaccineGroup : compiledVaccineGroups) {
                addVaccineGroup(-1, vaccineGroup, vaccineList, alerts);
            }
        } else {
            for (VaccineGroup vaccineGroup : vaccineGroups) {
                try {
                    vaccineGroup.setChildActive(isChildActive);
                    vaccineGroup.updateChildsActiveStatus();
                } catch (Exception e) {
                    Log.e(TAG, e.getMessage(), e);
                }
            }
        }

        showVaccineNotifications(vaccineList, alerts);
    }

    private void showVaccineNotifications(List<Vaccine> vaccineList, List<Alert> alerts) {

        DetailsRepository detailsRepository = CoreLibrary.getInstance().context().detailsRepository();
        Map<String, String> details = detailsRepository.getAllDetailsForClient(childDetails.entityId());

        if (details.containsKey(SHOW_BCG2_REMINDER) || details.containsKey(SHOW_BCG_SCAR)) {
            return;
        }

        if (registerClickables != null) {
            return;
        }

        if (VaccinateActionUtils.hasVaccine(vaccineList, VaccineRepo.Vaccine.bcg2)) {
            return;
        }

        Vaccine bcg = VaccinateActionUtils.getVaccine(vaccineList, VaccineRepo.Vaccine.bcg);
        if (bcg == null) {
            return;
        }

        Alert alert = VaccinateActionUtils.getAlert(alerts, VaccineRepo.Vaccine.bcg2);
        if (alert == null || alert.isComplete()) {
            return;
        }

        int bcgOffsetInWeeks = 12;
        Calendar twelveWeeksLaterDate = Calendar.getInstance();
        twelveWeeksLaterDate.setTime(bcg.getDate());
        twelveWeeksLaterDate.add(Calendar.WEEK_OF_YEAR, bcgOffsetInWeeks);

        Calendar today = Calendar.getInstance();

        if (today.getTime().after(twelveWeeksLaterDate.getTime()) || DateUtils.isSameDay(twelveWeeksLaterDate, today)) {
            showCheckBcgScarNotification(alert);
        }
    }

    private void addVaccineGroup(int canvasId, org.smartregister.immunization.domain.jsonmapping.VaccineGroup vaccineGroupData, List<Vaccine> vaccineList, List<Alert> alerts) {
        LinearLayout vaccineGroupCanvasLL = findViewById(R.id.vaccine_group_canvas_ll);
        VaccineGroup curGroup = new VaccineGroup(this);
        curGroup.setChildActive(isChildActive);
        curGroup.setData(vaccineGroupData, childDetails, vaccineList, alerts, Constants.KEY.CHILD);
        curGroup.setOnRecordAllClickListener(new VaccineGroup.OnRecordAllClickListener() {
            @Override
            public void onClick(VaccineGroup vaccineGroup, ArrayList<VaccineWrapper> dueVaccines) {
                if (dialogOpen) {
                    return;
                }

                dialogOpen = true;
                if (isChildActive) {
                    addVaccinationDialogFragment(dueVaccines, vaccineGroup);
                } else {
                    showActivateChildStatusDialogBox();
                }
            }
        });
        curGroup.setOnVaccineClickedListener(new VaccineGroup.OnVaccineClickedListener() {
            @Override
            public void onClick(VaccineGroup vaccineGroup, VaccineWrapper vaccine) {
                if (dialogOpen) {
                    return;
                }

                dialogOpen = true;
                if (isChildActive) {
                    ArrayList<VaccineWrapper> vaccineWrappers = new ArrayList<>();
                    vaccineWrappers.add(vaccine);
                    addVaccinationDialogFragment(vaccineWrappers, vaccineGroup);
                } else {
                    showActivateChildStatusDialogBox();
                }
            }
        });
        curGroup.setOnVaccineUndoClickListener(new VaccineGroup.OnVaccineUndoClickListener() {
            @Override
            public void onUndoClick(VaccineGroup vaccineGroup, VaccineWrapper vaccine) {
                if (dialogOpen) {
                    return;
                }

                dialogOpen = true;
                if (isChildActive) {
                    addVaccineUndoDialogFragment(vaccineGroup, vaccine);
                } else {
                    showActivateChildStatusDialogBox();
                }
            }
        });

        LinearLayout parent;
        int groupParentId = canvasId;
        if (groupParentId == -1) {
            Random r = new Random();
            groupParentId = r.nextInt(RANDOM_MAX_RANGE - RANDOM_MIN_RANGE) + RANDOM_MIN_RANGE;
            parent = new LinearLayout(this);
            parent.setId(groupParentId);
            vaccineGroupCanvasLL.addView(parent);
        } else {
            parent = findViewById(groupParentId);
            parent.removeAllViews();
        }
        parent.addView(curGroup);
        curGroup.setTag(R.id.vaccine_group_vaccine_data, vaccineGroupData.toString());
        curGroup.setTag(R.id.vaccine_group_parent_id, String.valueOf(groupParentId));
        vaccineGroups.add(curGroup);
    }

    private void addVaccineUndoDialogFragment(VaccineGroup vaccineGroup, VaccineWrapper vaccineWrapper) {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        Fragment prev = getFragmentManager().findFragmentByTag(DIALOG_TAG);
        if (prev != null) {
            ft.remove(prev);
        }

        ft.addToBackStack(null);
        vaccineGroup.setModalOpen(true);

        UndoVaccinationDialogFragment undoVaccinationDialogFragment = UndoVaccinationDialogFragment.newInstance(vaccineWrapper);
        undoVaccinationDialogFragment.show(ft, DIALOG_TAG);
        undoVaccinationDialogFragment.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                dialogOpen = false;
            }
        });
    }

    private void addServiceUndoDialogFragment(ServiceGroup serviceGroup, ServiceWrapper serviceWrapper) {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        Fragment prev = getFragmentManager().findFragmentByTag(DIALOG_TAG);
        if (prev != null) {
            ft.remove(prev);
        }

        ft.addToBackStack(null);
        serviceGroup.setModalOpen(true);

        UndoServiceDialogFragment undoServiceDialogFragment = UndoServiceDialogFragment.newInstance(serviceWrapper);
        undoServiceDialogFragment.show(ft, DIALOG_TAG);
        undoServiceDialogFragment.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                dialogOpen = false;
            }
        });
    }

    private void updateWeightViews(Weight lastUnsyncedWeight, final boolean isActive) {

        String childName = constructChildName();
        String gender = Utils.getValue(childDetails.getColumnmaps(), DBConstants.KEY.GENDER, true);
        String motherFirstName = Utils.getValue(childDetails.getColumnmaps(), DBConstants.KEY.MOTHER_FIRST_NAME, true);
        if (StringUtils.isBlank(childName) && StringUtils.isNotBlank(motherFirstName)) {
            childName = "B/o " + motherFirstName.trim();
        }

        String zeirId = Utils.getValue(childDetails.getColumnmaps(), DBConstants.KEY.ZEIR_ID, false);
        String duration = "";
        String dobString = Utils.getValue(childDetails.getColumnmaps(), Constants.EC_CHILD_TABLE.DOB, false);
        DateTime dateTime = Utils.dobStringToDateTime(dobString);
        if (dateTime != null) {
            duration = DateUtil.getDuration(dateTime);
        }

        Photo photo = ImageUtils.profilePhotoByClient(childDetails);

        WeightWrapper weightWrapper = new WeightWrapper();
        weightWrapper.setId(childDetails.entityId());
        weightWrapper.setGender(gender);
        weightWrapper.setPatientName(childName);
        weightWrapper.setPatientNumber(zeirId);
        weightWrapper.setPatientAge(duration);
        weightWrapper.setPhoto(photo);
        weightWrapper.setPmtctStatus(Utils.getValue(childDetails.getColumnmaps(), DBConstants.KEY.PMTCT_STATUS, false));

        if (lastUnsyncedWeight != null) {
            weightWrapper.setWeight(lastUnsyncedWeight.getKg());
            weightWrapper.setDbKey(lastUnsyncedWeight.getId());
            weightWrapper.setUpdatedWeightDate(new DateTime(lastUnsyncedWeight.getDate()), false);
        }

        updateRecordWeightViews(weightWrapper, isActive);

        ImageButton growthChartButton = findViewById(R.id.growth_chart_button);
        growthChartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.startAsyncTask(new ShowGrowthChartTask(), null);
            }
        });
    }

    private void updateRecordWeightViews(WeightWrapper weightWrapper, final boolean isActive) {
        View recordWeight = findViewById(R.id.record_weight);
        recordWeight.setClickable(true);
        recordWeight.setBackground(getResources().getDrawable(R.drawable.record_weight_bg));

        TextView recordWeightText = findViewById(R.id.record_weight_text);
        recordWeightText.setText(R.string.record_weight);
        if (!isActive) {
            recordWeightText.setTextColor(getResources().getColor(R.color.inactive_text_color));
        } else {
            recordWeightText.setTextColor(getResources().getColor(R.color.text_black));
        }

        ImageView recordWeightCheck = (ImageView) findViewById(R.id.record_weight_check);
        recordWeightCheck.setVisibility(View.GONE);
        recordWeight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isActive) {
                    showWeightDialog(view);
                } else {
                    showActivateChildStatusDialogBox();
                }
            }
        });

        if (weightWrapper.getDbKey() != null && weightWrapper.getWeight() != null) {
            recordWeightText.setText(Utils.kgStringSuffix(weightWrapper.getWeight()));
            recordWeightCheck.setVisibility(View.VISIBLE);

            if (weightWrapper.getUpdatedWeightDate() != null) {
                long timeDiff = Calendar.getInstance().getTimeInMillis() - weightWrapper.getUpdatedWeightDate().getMillis();

                if (timeDiff <= TimeUnit.MILLISECONDS.convert(RECORD_WEIGHT_BUTTON_ACTIVE_MIN, TimeUnit.HOURS)) {
                    //disable the button
                    recordWeight.setClickable(false);
                    recordWeight.setBackground(new ColorDrawable(getResources()
                            .getColor(android.R.color.transparent)));
                } else {
                    //reset state
                    weightWrapper.setWeight(null);
                    weightWrapper.setDbKey(null);
                    recordWeight.setClickable(true);
                    recordWeight.setBackground(getResources().getDrawable(R.drawable.record_weight_bg));
                    recordWeightText.setText(R.string.record_weight);
                    recordWeightCheck.setVisibility(View.GONE);
                }
            }
        }

        recordWeight.setTag(weightWrapper);

    }

    private void showWeightDialog(View view) {
        FragmentTransaction ft = this.getFragmentManager().beginTransaction();
        Fragment prev = this.getFragmentManager().findFragmentByTag(DIALOG_TAG);
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);

        String dobString = Utils.getValue(childDetails.getColumnmaps(), Constants.EC_CHILD_TABLE.DOB, false);
        Date dob = Utils.dobStringToDate(dobString);
        if (dob == null) {
            dob = Calendar.getInstance().getTime();
        }

        WeightWrapper weightWrapper = (WeightWrapper) view.getTag();
        RecordWeightDialogFragment recordWeightDialogFragment = RecordWeightDialogFragment.newInstance(dob, weightWrapper);
        recordWeightDialogFragment.show(ft, DIALOG_TAG);

    }

    private void showActivateChildStatusDialogBox() {
        String thirdPersonPronoun = getChildsThirdPersonPronoun(childDetails);
        String childsCurrentStatus = WordUtils.uncapitalize(getHumanFriendlyChildsStatus(childDetails), '-', ' ');
        FragmentTransaction ft = this.getFragmentManager().beginTransaction();
        Fragment prev = this.getFragmentManager().findFragmentByTag(DIALOG_TAG);
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);

        ActivateChildStatusDialogFragment activateChildStatusFragmentDialog = ActivateChildStatusDialogFragment.newInstance(thirdPersonPronoun, childsCurrentStatus, R.style.PathAlertDialog);
        activateChildStatusFragmentDialog.setOnClickListener(new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == DialogInterface.BUTTON_POSITIVE) {
                    SaveChildsStatusTask saveChildsStatusTask = new SaveChildsStatusTask();
                    Utils.startAsyncTask(saveChildsStatusTask, null);
                }
            }
        });
        activateChildStatusFragmentDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                dialogOpen = false;
            }
        });
        activateChildStatusFragmentDialog.show(ft, DIALOG_TAG);
    }

    private String getChildsThirdPersonPronoun(CommonPersonObjectClient childDetails) {
        String genderString = Utils.getValue(childDetails, DBConstants.KEY.GENDER, false);
        if (genderString != null && genderString.toLowerCase().equals(Constants.GENDER.FEMALE)) {
            return getString(R.string.her);
        } else if (genderString != null && genderString.toLowerCase().equals(Constants.GENDER.MALE)) {
            return getString(R.string.him);
        }

        return getString(R.string.her) + "/" + getString(R.string.him);
    }

    private void activateChildsStatus() {
        try {
            Map<String, String> details = childDetails.getColumnmaps();
            if (details.containsKey(Constants.CHILD_STATUS.INACTIVE) && details.get(Constants.CHILD_STATUS.INACTIVE) != null && details.get(Constants.CHILD_STATUS.INACTIVE).equalsIgnoreCase(Boolean.TRUE.toString())) {
                childDetails.setColumnmaps(JsonFormUtils.updateClientAttribute(this, childDetails, Constants.CHILD_STATUS.INACTIVE, false));
            }

            if (details.containsKey(Constants.CHILD_STATUS.LOST_TO_FOLLOW_UP) && details.get(Constants.CHILD_STATUS.LOST_TO_FOLLOW_UP) != null && details.get(Constants.CHILD_STATUS.LOST_TO_FOLLOW_UP).equalsIgnoreCase(Boolean.TRUE.toString())) {
                childDetails.setColumnmaps(JsonFormUtils.updateClientAttribute(this, childDetails, Constants.CHILD_STATUS.LOST_TO_FOLLOW_UP, false));
            }
        } catch (Exception e) {
            Log.e(TAG, Log.getStackTraceString(e));
        }
    }

    private String readAssetContents(String path) {
        String fileContents = null;
        try {
            InputStream is = getAssets().open(path);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            fileContents = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            Log.e(TAG, ex.toString(), ex);
        }

        return fileContents;
    }

    public static void launchActivity(Context fromContext, CommonPersonObjectClient childDetails, RegisterClickables registerClickables) {
        Intent intent = new Intent(fromContext, Utils.metadata().childImmunizationActivity);
        Bundle bundle = new Bundle();
        bundle.putSerializable(Constants.INTENT_KEY.EXTRA_CHILD_DETAILS, childDetails);
        bundle.putSerializable(Constants.INTENT_KEY.EXTRA_REGISTER_CLICKABLES, registerClickables);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtras(bundle);

        fromContext.startActivity(intent);
    }

    public abstract void launchDetailActivity(Context fromContext, CommonPersonObjectClient childDetails, RegisterClickables registerClickables);

    private String updateActivityTitle() {
        String name = "";
        if (isDataOk()) {
            name = constructChildName();
        }
//        return String.format("%s > %s", getString(R.string.app_name), name.trim());
        return name.trim();

    }

    private void showChildsStatus(CommonPersonObjectClient child) {
        String status = getHumanFriendlyChildsStatus(child);
        showChildsStatus(status);
    }

    @Override
    public void onLocationChanged(final String newLocation) {
        // TODO: Do whatever needs to be done when the location is changed
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_child_immunization;
    }

    @Override
    protected abstract int getDrawerLayoutId();//Navigation drawer ID ?

    @Override
    protected int getToolbarId() {
        return LocationSwitcherToolbar.TOOLBAR_ID;
    }

    @Override
    protected Class onBackActivity() {
        return BaseChildRegisterActivity.class;
    }

    @Override
    public void onWeightTaken(WeightWrapper tag) {
        if (tag != null) {

            String genderString = Utils.getValue(childDetails, DBConstants.KEY.GENDER, false);
            tag.setGender(genderString);

            String dobString = Utils.getValue(childDetails.getColumnmaps(), Constants.EC_CHILD_TABLE.DOB, false);

            Utils.recordWeight(GrowthMonitoringLibrary.getInstance().weightRepository(), tag, dobString);

            updateRecordWeightViews(tag, isActiveStatus(childDetails));
            setLastModified(true);
        }
    }

    @Override
    public void onVaccinateToday(ArrayList<VaccineWrapper> tags, View v) {
        if (tags != null && !tags.isEmpty()) {
            View view = getLastOpenedView();
            saveVaccine(tags, view);
        }
    }

    @Override
    public void onVaccinateEarlier(ArrayList<VaccineWrapper> tags, View v) {
        if (tags != null && !tags.isEmpty()) {
            View view = getLastOpenedView();
            saveVaccine(tags, view);
        }
    }

    @Override
    public void onUndoVaccination(VaccineWrapper tag, View v) {
        Utils.startAsyncTask(new UndoVaccineTask(tag, v), null);
    }

    private void addVaccinationDialogFragment(ArrayList<VaccineWrapper> vaccineWrappers, VaccineGroup vaccineGroup) {

        FragmentTransaction ft = this.getFragmentManager().beginTransaction();
        Fragment prev = this.getFragmentManager().findFragmentByTag(DIALOG_TAG);
        if (prev != null) {
            ft.remove(prev);
        }

        ft.addToBackStack(null);
        vaccineGroup.setModalOpen(true);
        String dobString = Utils.getValue(childDetails.getColumnmaps(), Constants.EC_CHILD_TABLE.DOB, false);
        Date dob = Utils.dobStringToDate(dobString);
        if (dob == null) {
            dob = Calendar.getInstance().getTime();
        }

        List<Vaccine> vaccineList = ImmunizationLibrary.getInstance().vaccineRepository().findByEntityId(childDetails.entityId());
        if (vaccineList == null) {
            vaccineList = new ArrayList<>();
        }

        VaccinationDialogFragment vaccinationDialogFragment = VaccinationDialogFragment.newInstance(dob, vaccineList, vaccineWrappers, true);
        vaccinationDialogFragment.show(ft, DIALOG_TAG);
        vaccinationDialogFragment.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                dialogOpen = false;
            }
        });

    }

    private void addServiceDialogFragment(ServiceWrapper serviceWrapper, ServiceGroup serviceGroup) {

        FragmentTransaction ft = this.getFragmentManager().beginTransaction();
        Fragment prev = this.getFragmentManager().findFragmentByTag(DIALOG_TAG);
        if (prev != null) {
            ft.remove(prev);
        }

        ft.addToBackStack(null);
        serviceGroup.setModalOpen(true);

        String dobString = Utils.getValue(childDetails.getColumnmaps(), Constants.EC_CHILD_TABLE.DOB, false);
        DateTime dob = Utils.dobStringToDateTime(dobString);
        if (dob == null) {
            dob = DateTime.now();
        }

        List<ServiceRecord> serviceRecordList = ImmunizationLibrary.getInstance().recurringServiceRecordRepository().findByEntityId(childDetails.entityId());
        if (serviceRecordList == null) {
            serviceRecordList = new ArrayList<>();
        }

        ServiceDialogFragment serviceDialogFragment = ServiceDialogFragment.newInstance(dob, serviceRecordList, serviceWrapper, true);
        serviceDialogFragment.show(ft, DIALOG_TAG);
        serviceDialogFragment.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                dialogOpen = false;
            }
        });
    }

    private void performRegisterActions() {
        if (registerClickables != null) {
            if (registerClickables.isRecordWeight()) {
                final View recordWeight = findViewById(R.id.record_weight);
                recordWeight.post(new Runnable() {
                    @Override
                    public void run() {
                        recordWeight.performClick();
                    }
                });
            } else if (registerClickables.isRecordAll()) {
                performRecordAllClick(0);
            }

            //Reset register actions
            registerClickables.setRecordAll(false);
            registerClickables.setRecordWeight(false);
        }
    }

    private void performRecordAllClick(final int index) {
        if (vaccineGroups != null && vaccineGroups.size() > index) {
            final VaccineGroup vaccineGroup = vaccineGroups.get(index);
            vaccineGroup.post(new Runnable() {
                @Override
                public void run() {
                    vaccineGroup.setVaccineCardAdapterLoadingListener(new VaccineCardAdapterLoadingListener() {
                        @Override
                        public void onFinishedLoadingVaccineWrappers() {
                            ArrayList<VaccineWrapper> vaccineWrappers = vaccineGroup.getDueVaccines();
                            if (!vaccineWrappers.isEmpty()) {
                                final TextView recordAllTV = vaccineGroup.findViewById(R.id.record_all_tv);
                                recordAllTV.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        recordAllTV.performClick();
                                    }
                                });
                            } else {
                                performRecordAllClick(index + 1);
                            }
                        }
                    });
                }
            });
        }
    }

    private void saveVaccine(ArrayList<VaccineWrapper> tags, final View view) {
        if (tags.isEmpty()) {
            return;
        }

        VaccineWrapper[] arrayTags = tags.toArray(new VaccineWrapper[tags.size()]);
        SaveVaccinesTask backgroundTask = new SaveVaccinesTask();
        backgroundTask.setVaccineRepository(ImmunizationLibrary.getInstance().vaccineRepository());
        backgroundTask.setView(view);
        Utils.startAsyncTask(backgroundTask, arrayTags);

    }

    private void saveVaccine(VaccineRepository vaccineRepository, VaccineWrapper tag) {
        if (tag.getUpdatedVaccineDate() == null) {
            return;
        }


        Vaccine vaccine = new Vaccine();
        if (tag.getDbKey() != null) {
            vaccine = vaccineRepository.find(tag.getDbKey());
        }
        vaccine.setBaseEntityId(childDetails.entityId());
        vaccine.setName(tag.getName());
        vaccine.setDate(tag.getUpdatedVaccineDate().toDate());
        vaccine.setAnmId(getOpenSRPContext().allSharedPreferences().fetchRegisteredANM());
        vaccine.setLocationId(LocationHelper.getInstance().getOpenMrsLocationId(toolbar.getCurrentLocation()));

        String lastChar = vaccine.getName().substring(vaccine.getName().length() - 1);
        if (StringUtils.isNumeric(lastChar)) {
            vaccine.setCalculation(Integer.valueOf(lastChar));
        } else {
            vaccine.setCalculation(-1);
        }
        Utils.addVaccine(vaccineRepository, vaccine);
        tag.setDbKey(vaccine.getId());
        setLastModified(true);
    }

    private Vaccine createDummyVaccine(String name, Date date, String syncStatus) {
        Vaccine vaccine = new Vaccine();
        vaccine.setId(-1l);
        vaccine.setBaseEntityId(childDetails.entityId());
        vaccine.setName(name);
        vaccine.setDate(date);
        vaccine.setAnmId(getOpenSRPContext().allSharedPreferences().fetchRegisteredANM());
        vaccine.setLocationId(LocationHelper.getInstance().getOpenMrsLocationId(toolbar.getCurrentLocation()));
        vaccine.setSyncStatus(syncStatus);
        vaccine.setFormSubmissionId(JsonFormUtils.generateRandomUUIDString());
        vaccine.setUpdatedAt(new Date().getTime());

        String lastChar = vaccine.getName().substring(vaccine.getName().length() - 1);
        if (StringUtils.isNumeric(lastChar)) {
            vaccine.setCalculation(Integer.valueOf(lastChar));
        } else {
            vaccine.setCalculation(-1);
        }
        return vaccine;
    }

    private void updateVaccineGroupViews(View view, final ArrayList<VaccineWrapper> wrappers, List<Vaccine> vaccineList) {
        updateVaccineGroupViews(view, wrappers, vaccineList, false);
    }

    private void updateVaccineGroupViews(View view, final ArrayList<VaccineWrapper> wrappers, final List<Vaccine> vaccineList, final boolean undo) {
        if (view == null || !(view instanceof VaccineGroup)) {
            return;
        }
        final VaccineGroup vaccineGroup = (VaccineGroup) view;
        vaccineGroup.setModalOpen(false);

        if (Looper.myLooper() == Looper.getMainLooper()) {
            if (undo) {
                vaccineGroup.setVaccineList(vaccineList);
                vaccineGroup.updateWrapperStatus(wrappers, Constants.KEY.CHILD);
            }
            vaccineGroup.updateViews(wrappers);

        } else {
            Handler handler = new Handler(Looper.getMainLooper());
            handler.post(new Runnable() {
                @Override
                public void run() {
                    if (undo) {
                        vaccineGroup.setVaccineList(vaccineList);
                        vaccineGroup.updateWrapperStatus(wrappers, Constants.KEY.CHILD);
                    }
                    vaccineGroup.updateViews(wrappers);
                }
            });
        }
    }

    private void showRecordWeightNotification() {
        if (!weightNotificationShown) {
            weightNotificationShown = true;
            showNotification(R.string.record_weight_notification, R.drawable.ic_weight_notification,
                    R.string.record_weight,
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            View recordWeight = findViewById(R.id.record_weight);
                            showWeightDialog(recordWeight);
                            hideNotification();
                        }
                    }, R.string.cancel, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            hideNotification();
                        }
                    }, null);
        }
    }

    private void showCheckBcgScarNotification(Alert alert) {
        if (!bcgScarNotificationShown) {
            bcgScarNotificationShown = true;
            final ViewGroup rootView = (ViewGroup) ((ViewGroup) findViewById(android.R.id.content)).getChildAt(0);

            new BCGNotificationDialog(this, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    onBcgReminderOptionSelected(SHOW_BCG_SCAR);
                    Snackbar.make(rootView, R.string.turn_off_reminder_notification_message, Snackbar.LENGTH_LONG).show();
                }
            }, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    onBcgReminderOptionSelected(SHOW_BCG2_REMINDER);
                    Snackbar.make(rootView, R.string.create_reminder_notification_message, Snackbar.LENGTH_LONG).show();
                }
            }).show();
        }
    }

    private String constructChildName() {
        String firstName = Utils.getValue(childDetails.getColumnmaps(), DBConstants.KEY.FIRST_NAME, true);
        String lastName = Utils.getValue(childDetails.getColumnmaps(), DBConstants.KEY.LAST_NAME, true);
        return Utils.getName(firstName, lastName).trim();
    }

    @Override
    public void finish() {
        if (isLastModified()) {
            String tableName = Utils.metadata().childRegister.tableName;
            AllCommonsRepository allCommonsRepository = getOpenSRPContext().allCommonsRepositoryobjects(tableName);
            ContentValues contentValues = new ContentValues();
            contentValues.put(DBConstants.KEY.LAST_INTERACTED_WITH, (new Date()).getTime());
            allCommonsRepository.update(tableName, contentValues, childDetails.entityId());
            allCommonsRepository.updateSearch(childDetails.entityId());
        }
        super.finish();
    }

    public abstract boolean isLastModified();

    public abstract void setLastModified(boolean lastModified);

    private VaccineGroup getLastOpenedView() {
        if (vaccineGroups == null) {
            return null;
        }

        for (VaccineGroup vaccineGroup : vaccineGroups) {
            if (vaccineGroup.isModalOpen()) {
                return vaccineGroup;
            }
        }

        return null;
    }

    private void updateVaccineGroupsUsingAlerts(List<String> affectedVaccines, List<Vaccine> vaccineList, List<Alert> alerts) {
        if (affectedVaccines != null && vaccineList != null) {
            // Update all other affected vaccine groups
            HashMap<VaccineGroup, ArrayList<VaccineWrapper>> affectedGroups = new HashMap<>();
            for (String curAffectedVaccineName : affectedVaccines) {
                boolean viewFound = false;
                // Check what group it is in
                for (VaccineGroup curGroup : vaccineGroups) {
                    ArrayList<VaccineWrapper> groupWrappers = curGroup.getAllVaccineWrappers();
                    if (groupWrappers == null) groupWrappers = new ArrayList<>();
                    for (VaccineWrapper curWrapper : groupWrappers) {
                        String curWrapperName = curWrapper.getName();

                        // Check if current wrapper is one of the combined vaccines
                        if (COMBINED_VACCINES.contains(curWrapperName)) {
                            // Check if any of the sister vaccines is currAffectedVaccineName
                            String[] allSisters = COMBINED_VACCINES_MAP.get(curWrapperName).split(" / ");
                            for (String allSister : allSisters) {
                                if (allSister.replace(" ", "").equalsIgnoreCase(curAffectedVaccineName.replace(" ", ""))) {
                                    curWrapperName = allSister;
                                    break;
                                }
                            }
                        }

                        if (curWrapperName.replace(" ", "").toLowerCase()
                                .contains(curAffectedVaccineName.replace(" ", "").toLowerCase())) {
                            if (!affectedGroups.containsKey(curGroup)) {
                                affectedGroups.put(curGroup, new ArrayList<VaccineWrapper>());
                            }

                            affectedGroups.get(curGroup).add(curWrapper);
                            viewFound = true;
                        }

                        if (viewFound) break;
                    }

                    if (viewFound) break;
                }
            }

            for (VaccineGroup curGroup : affectedGroups.keySet()) {
                try {
                    vaccineGroups.remove(curGroup);
                    addVaccineGroup(Integer.valueOf((String) curGroup.getTag(R.id.vaccine_group_parent_id)),
                            curGroup.getVaccineData(),
                            vaccineList, alerts);
                } catch (Exception e) {
                    Log.e(TAG, Log.getStackTraceString(e));
                }
            }
        }
    }

    //Recurring Service
    @Override
    public void onGiveToday(ServiceWrapper tag, View v) {
        if (tag != null) {
            View view = RecurringServiceUtils.getLastOpenedServiceView(serviceGroups);
            saveService(tag, view);
        }
    }

    @Override
    public void onGiveEarlier(ServiceWrapper tag, View v) {
        if (tag != null) {
            View view = RecurringServiceUtils.getLastOpenedServiceView(serviceGroups);
            saveService(tag, view);
        }
    }

    @Override
    public void onUndoService(ServiceWrapper tag, View v) {
        Utils.startAsyncTask(new UndoServiceTask(tag), null);
    }

    private void saveService(ServiceWrapper tag, final View view) {
        if (tag == null) {
            return;
        }

        ServiceWrapper[] arrayTags = {tag};
        SaveServiceTask backgroundTask = new SaveServiceTask();
        String providerId = getOpenSRPContext().allSharedPreferences().fetchRegisteredANM();
        String locationId = LocationHelper.getInstance().getOpenMrsLocationId(toolbar.getCurrentLocation());

        backgroundTask.setProviderId(providerId);
        backgroundTask.setLocationId(locationId);
        backgroundTask.setView(view);
        Utils.startAsyncTask(backgroundTask, arrayTags);
    }

    public void onBcgReminderOptionSelected(String option) {

        final long DATE = new Date().getTime();

        switch (option) {

            case SHOW_BCG2_REMINDER:
                detailsRepository.add(childDetails.entityId(), SHOW_BCG2_REMINDER, Boolean.TRUE.toString(), DATE);
                break;

            case SHOW_BCG_SCAR:
                detailsRepository.add(childDetails.entityId(), SHOW_BCG_SCAR, String.valueOf(DATE), DATE);

                String providerId = getOpenSRPContext().allSharedPreferences().fetchRegisteredANM();
                String locationId = LocationHelper.getInstance().getOpenMrsLocationId(toolbar.getCurrentLocation());
                JsonFormUtils.createBCGScarEvent(getActivity(), childDetails.entityId(), providerId, locationId);
                break;

            default:
                break;
        }


        LinearLayout vaccineGroupCanvasLL = (LinearLayout) findViewById(R.id.vaccine_group_canvas_ll);
        vaccineGroupCanvasLL.removeAllViews();
        vaccineGroups = null;
        updateViews();
    }

    public org.smartregister.immunization.domain.jsonmapping.Vaccine getVaccineByName(@NonNull List<org.smartregister.immunization.domain.jsonmapping.Vaccine> vaccineList, @NonNull String name) {

        for (org.smartregister.immunization.domain.jsonmapping.Vaccine vaccine : vaccineList) {
            if (vaccine.name.equals(name))
                return vaccine;
        }
        return null;
    }

    public Vaccine getVaccineAquiredByName(@NonNull List<Vaccine> vaccineList, @NonNull String name) {

        for (Vaccine vaccine : vaccineList) {
            if (vaccine.getName().equals(name))
                return vaccine;
        }
        return null;
    }


    public void updateVaccineName(org.smartregister.immunization.domain.jsonmapping.Vaccine vaccine, @NonNull String newName) {

        if (vaccine != null)
            vaccine.name = newName;
    }

    public org.smartregister.immunization.domain.jsonmapping.VaccineGroup getVaccineGroupByName(@NonNull List<org.smartregister.immunization.domain.jsonmapping.VaccineGroup> vaccineGroupList, @NonNull String name) {

        for (org.smartregister.immunization.domain.jsonmapping.VaccineGroup vaccineGroup : vaccineGroupList) {
            if (vaccineGroup.name.equals(name))
                return vaccineGroup;
        }
        return null;
    }

    public static Object clone(@NonNull Object object) {

        Gson gson = new Gson();
        String serializedOject = gson.toJson(object);

        return gson.fromJson(serializedOject, object.getClass());
    }

    public List<org.smartregister.immunization.domain.jsonmapping.Vaccine> getJsonVaccineGroup(@NonNull String filename) {

        Class<List<org.smartregister.immunization.domain.jsonmapping.Vaccine>> classType = (Class) List.class;
        Type listType = new TypeToken<List<org.smartregister.immunization.domain.jsonmapping.Vaccine>>() {
        }.getType();
        return ImmunizationLibrary.getInstance().assetJsonToJava(filename, classType, listType);
    }

    ////////////////////////////////////////////////////////////////
    // Inner classes
    ////////////////////////////////////////////////////////////////

    private class UpdateViewTask extends AsyncTask<Void, Void, Map<String, NamedObject<?>>> {

        private VaccineRepository vaccineRepository;
        private WeightRepository weightRepository;
        private RecurringServiceTypeRepository recurringServiceTypeRepository;
        private RecurringServiceRecordRepository recurringServiceRecordRepository;
        private AlertService alertService;

        public void setVaccineRepository(VaccineRepository vaccineRepository) {
            this.vaccineRepository = vaccineRepository;
        }

        public void setWeightRepository(WeightRepository weightRepository) {
            this.weightRepository = weightRepository;
        }

        public void setRecurringServiceTypeRepository(RecurringServiceTypeRepository recurringServiceTypeRepository) {
            this.recurringServiceTypeRepository = recurringServiceTypeRepository;
        }

        public void setRecurringServiceRecordRepository(RecurringServiceRecordRepository recurringServiceRecordRepository) {
            this.recurringServiceRecordRepository = recurringServiceRecordRepository;
        }

        public void setAlertService(AlertService alertService) {
            this.alertService = alertService;
        }


        @Override
        protected void onPreExecute() {
            showProgressDialog(getString(R.string.updating_dialog_title), null);
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void onPostExecute(Map<String, NamedObject<?>> map) {

            List<Vaccine> vaccineList = AsyncTaskUtils.extractVaccines(map);
            Map<String, List<ServiceType>> serviceTypeMap = AsyncTaskUtils.extractServiceTypes(map);
            List<ServiceRecord> serviceRecords = AsyncTaskUtils.extractServiceRecords(map);
            List<Alert> alertList = AsyncTaskUtils.extractAlerts(map);
            Weight weight = AsyncTaskUtils.retriveWeight(map);

            updateWeightViews(weight, isChildActive);
            updateServiceViews(serviceTypeMap, serviceRecords, alertList);
            // TO DO: Needs fixing
            //updateVaccinationViews(vaccineList, alertList);
            performRegisterActions();

            hideProgressDialog();
        }

        @Override
        protected Map<String, NamedObject<?>> doInBackground(Void... voids) {
            String dobString = Utils.getValue(childDetails.getColumnmaps(), Constants.EC_CHILD_TABLE.DOB, false);
            DateTime dateTime = Utils.dobStringToDateTime(dobString);
            if (dateTime != null) {
                VaccineSchedule.updateOfflineAlerts(childDetails.entityId(), dateTime, Constants.KEY.CHILD);
                ServiceSchedule.updateOfflineAlerts(childDetails.entityId(), dateTime);
            }

            List<Vaccine> vaccineList = new ArrayList<>();
            Weight weight = null;

            Map<String, List<ServiceType>> serviceTypeMap = new LinkedHashMap<>();
            List<ServiceRecord> serviceRecords = new ArrayList<>();

            List<Alert> alertList = new ArrayList<>();
            if (vaccineRepository != null) {
                vaccineList = vaccineRepository.findByEntityId(childDetails.entityId());

            }
            if (weightRepository != null) {
                weight = weightRepository.findUnSyncedByEntityId(childDetails.entityId());
            }

            if (recurringServiceRecordRepository != null) {
                serviceRecords = recurringServiceRecordRepository.findByEntityId(childDetails.entityId());
            }

            if (recurringServiceTypeRepository != null) {
                List<ServiceType> serviceTypes = recurringServiceTypeRepository.fetchAll();
                for (ServiceType serviceType : serviceTypes) {
                    String type = serviceType.getType();
                    List<ServiceType> serviceTypeList = serviceTypeMap.get(type);
                    if (serviceTypeList == null) {
                        serviceTypeList = new ArrayList<>();
                    }
                    serviceTypeList.add(serviceType);
                    serviceTypeMap.put(type, serviceTypeList);
                }
            }

            if (alertService != null) {
                alertList = alertService.findByEntityId(childDetails.entityId());
            }

            Map<String, NamedObject<?>> map = new HashMap<>();

            NamedObject<List<Vaccine>> vaccineNamedObject = new NamedObject<>(Vaccine.class.getName(), vaccineList);
            map.put(vaccineNamedObject.name, vaccineNamedObject);

            NamedObject<Weight> weightNamedObject = new NamedObject<>(Weight.class.getName(), weight);
            map.put(weightNamedObject.name, weightNamedObject);

            NamedObject<Map<String, List<ServiceType>>> serviceTypeNamedObject = new NamedObject<>(ServiceType.class.getName(), serviceTypeMap);
            map.put(serviceTypeNamedObject.name, serviceTypeNamedObject);

            NamedObject<List<ServiceRecord>> serviceRecordNamedObject = new NamedObject<>(ServiceRecord.class.getName(), serviceRecords);
            map.put(serviceRecordNamedObject.name, serviceRecordNamedObject);

            NamedObject<List<Alert>> alertsNamedObject = new NamedObject<>(Alert.class.getName(), alertList);
            map.put(alertsNamedObject.name, alertsNamedObject);

            return map;
        }
    }

    public class SaveServiceTask extends AsyncTask<ServiceWrapper, Void, Triple<ArrayList<ServiceWrapper>, List<ServiceRecord>, List<Alert>>> {

        private View view;
        private String providerId;
        private String locationId;

        public void setView(View view) {
            this.view = view;
        }

        public void setProviderId(String providerId) {
            this.providerId = providerId;
        }

        public void setLocationId(String locationId) {
            this.locationId = locationId;
        }

        @Override
        protected void onPreExecute() {
            showProgressDialog();
        }

        @Override
        protected void onPostExecute(Triple<ArrayList<ServiceWrapper>, List<ServiceRecord>, List<Alert>> triple) {
            hideProgressDialog();
            RecurringServiceUtils.updateServiceGroupViews(view, triple.getLeft(), triple.getMiddle(), triple.getRight());
        }

        @Override
        protected Triple<ArrayList<ServiceWrapper>, List<ServiceRecord>, List<Alert>> doInBackground(ServiceWrapper... params) {

            ArrayList<ServiceWrapper> list = new ArrayList<>();

            for (ServiceWrapper tag : params) {
                RecurringServiceUtils.saveService(tag, childDetails.entityId(), providerId, locationId);
                setLastModified(true);
                list.add(tag);


                ServiceSchedule.updateOfflineAlerts(tag.getType(), childDetails.entityId(), Utils.dobToDateTime(childDetails));
            }

            List<ServiceRecord> serviceRecordList = ImmunizationLibrary.getInstance().recurringServiceRecordRepository().findByEntityId(childDetails.entityId());

            AlertService alertService = getOpenSRPContext().alertService();
            List<Alert> alertList = alertService.findByEntityId(childDetails.entityId());

            return Triple.of(list, serviceRecordList, alertList);

        }
    }

    private class UndoServiceTask extends AsyncTask<Void, Void, Void> {

        private final View view;
        private final ServiceWrapper tag;
        private List<ServiceRecord> serviceRecordList;
        private ArrayList<ServiceWrapper> wrappers;
        private List<Alert> alertList;

        public UndoServiceTask(ServiceWrapper tag) {
            this.tag = tag;
            this.view = RecurringServiceUtils.getLastOpenedServiceView(serviceGroups);
        }

        @Override
        protected void onPreExecute() {
            showProgressDialog(getString(R.string.updating_dialog_title), null);
        }

        @Override
        protected Void doInBackground(Void... params) {
            if (tag != null && tag.getDbKey() != null) {
                Long dbKey = tag.getDbKey();
                ImmunizationLibrary.getInstance().recurringServiceRecordRepository().deleteServiceRecord(dbKey);

                serviceRecordList = ImmunizationLibrary.getInstance().recurringServiceRecordRepository().findByEntityId(childDetails.entityId());

                wrappers = new ArrayList<>();
                wrappers.add(tag);

                ServiceSchedule.updateOfflineAlerts(tag.getType(), childDetails.entityId(), Utils.dobToDateTime(childDetails));

                AlertService alertService = getOpenSRPContext().alertService();
                alertList = alertService.findByEntityId(childDetails.entityId());

            }
            return null;
        }

        @Override
        protected void onPostExecute(Void params) {
            super.onPostExecute(params);
            hideProgressDialog();

            tag.setUpdatedVaccineDate(null, false);
            tag.setDbKey(null);

            RecurringServiceUtils.updateServiceGroupViews(view, wrappers, serviceRecordList, alertList, true);
        }
    }

    private class ShowGrowthChartTask extends AsyncTask<Void, Void, List<Weight>> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgressDialog();
        }

        @Override
        protected List<Weight> doInBackground(Void... params) {
            List<Weight> allWeights = GrowthMonitoringLibrary.getInstance().weightRepository().findByEntityId(childDetails.entityId());
            try {
                String dobString = Utils.getValue(childDetails.getColumnmaps(), Constants.EC_CHILD_TABLE.DOB, false);
                Date dob = Utils.dobStringToDate(dobString);
                if (!TextUtils.isEmpty(Utils.getValue(childDetails.getColumnmaps(), Constants.KEY.BIRTH_WEIGHT, false))
                        && dob != null) {
                    Double birthWeight = Double.valueOf(Utils.getValue(childDetails.getColumnmaps(), Constants.KEY.BIRTH_WEIGHT, false));

                    Weight weight = new Weight(-1l, null, (float) birthWeight.doubleValue(), dob, null, null, null, Calendar.getInstance().getTimeInMillis(), null, null, 0);
                    allWeights.add(weight);
                }
            } catch (Exception e) {
                Log.e(TAG, Log.getStackTraceString(e));
            }

            return allWeights;
        }

        @Override
        protected void onPostExecute(List<Weight> allWeights) {
            super.onPostExecute(allWeights);
            hideProgressDialog();
            FragmentTransaction ft = getActivity().getFragmentManager().beginTransaction();
            Fragment prev = getActivity().getFragmentManager().findFragmentByTag(DIALOG_TAG);
            if (prev != null) {
                ft.remove(prev);
            }
            ft.addToBackStack(null);


            GrowthDialogFragment growthDialogFragment = GrowthDialogFragment.newInstance(childDetails, allWeights);
            growthDialogFragment.show(ft, DIALOG_TAG);
        }
    }

    private class SaveVaccinesTask extends AsyncTask<VaccineWrapper, Void, ArrayList<VaccineWrapper>> {

        private View view;
        private VaccineRepository vaccineRepository;
        private AlertService alertService;
        private List<String> affectedVaccines;
        private List<Vaccine> vaccineList;
        private List<Alert> alertList;

        public void setView(View view) {
            this.view = view;
        }

        public void setVaccineRepository(VaccineRepository vaccineRepository) {
            this.vaccineRepository = vaccineRepository;
            alertService = getOpenSRPContext().alertService();
            affectedVaccines = new ArrayList<>();
        }

        @Override
        protected void onPreExecute() {
            showProgressDialog();
        }

        @Override
        protected void onPostExecute(ArrayList<VaccineWrapper> list) {
            hideProgressDialog();
            updateVaccineGroupViews(view, list, vaccineList);
            View recordWeight = findViewById(R.id.record_weight);
            WeightWrapper weightWrapper = (WeightWrapper) recordWeight.getTag();
            if (Configurable.isShowWeightPopUp && (weightWrapper == null || weightWrapper.getWeight() == null)) {
                showRecordWeightNotification();
            }

            updateVaccineGroupsUsingAlerts(affectedVaccines, vaccineList, alertList);
            showVaccineNotifications(vaccineList, alertList);
        }

        @Override
        protected ArrayList<VaccineWrapper> doInBackground(VaccineWrapper... vaccineWrappers) {

            ArrayList<VaccineWrapper> list = new ArrayList<>();
            if (vaccineRepository != null) {
                for (VaccineWrapper tag : vaccineWrappers) {
                    saveVaccine(vaccineRepository, tag);
                    list.add(tag);
                }
            }

            String dobString = Utils.getValue(childDetails.getColumnmaps(), Constants.EC_CHILD_TABLE.DOB, false);
            DateTime dateTime = Utils.dobStringToDateTime(dobString);
            if (dateTime != null) {
                affectedVaccines = VaccineSchedule.updateOfflineAlerts(childDetails.entityId(), dateTime, Constants.KEY.CHILD);
            }
            vaccineList = vaccineRepository.findByEntityId(childDetails.entityId());
            alertList = alertService.findByEntityId(childDetails.entityId());

            return list;
        }
    }


    private class UndoVaccineTask extends AsyncTask<Void, Void, Void> {

        private final VaccineWrapper tag;
        private final View v;
        private final VaccineRepository vaccineRepository;
        private final AlertService alertService;
        private List<Vaccine> vaccineList;
        private List<Alert> alertList;
        private List<String> affectedVaccines;

        public UndoVaccineTask(VaccineWrapper tag, View v) {
            this.tag = tag;
            this.v = v;
            vaccineRepository = ImmunizationLibrary.getInstance().vaccineRepository();
            alertService = getOpenSRPContext().alertService();
        }

        @Override
        protected void onPreExecute() {
            showProgressDialog(getString(R.string.updating_dialog_title), null);
        }

        @Override
        protected Void doInBackground(Void... params) {
            if (tag != null) {

                if (tag.getDbKey() != null) {
                    Long dbKey = tag.getDbKey();
                    vaccineRepository.deleteVaccine(dbKey);


                    String dobString = Utils.getValue(childDetails.getColumnmaps(), Constants.EC_CHILD_TABLE.DOB, false);
                    DateTime dateTime = Utils.dobStringToDateTime(dobString);
                    if (dateTime != null) {
                        affectedVaccines = VaccineSchedule.updateOfflineAlerts(childDetails.entityId(), dateTime, Constants.KEY.CHILD);
                        vaccineList = vaccineRepository.findByEntityId(childDetails.entityId());
                        alertList = alertService.findByEntityId(childDetails.entityId());
                    }
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void params) {
            hideProgressDialog();
            super.onPostExecute(params);

            // Refresh the vaccine group with the updated vaccine
            tag.setUpdatedVaccineDate(null, false);
            tag.setDbKey(null);

            View view = getLastOpenedView();

            ArrayList<VaccineWrapper> wrappers = new ArrayList<>();
            wrappers.add(tag);
            updateVaccineGroupViews(view, wrappers, vaccineList, true);
            updateVaccineGroupsUsingAlerts(affectedVaccines, vaccineList, alertList);
            showVaccineNotifications(vaccineList, alertList);
        }
    }

    private class GetSiblingsTask extends AsyncTask<Void, Void, ArrayList<String>> {

        @Override
        protected ArrayList<String> doInBackground(Void... params) {
            String baseEntityId = childDetails.entityId();
            String motherBaseEntityId = Utils.getValue(childDetails.getColumnmaps(), DBConstants.KEY.RELATIONAL_ID, false);
            if (!TextUtils.isEmpty(motherBaseEntityId) && !TextUtils.isEmpty(baseEntityId)) {
                List<CommonPersonObject> children = getOpenSRPContext().commonrepository(Utils.metadata().childRegister.tableName)
                        .findByRelational_IDs(motherBaseEntityId);

                if (children != null) {
                    ArrayList<String> baseEntityIds = new ArrayList<>();
                    for (CommonPersonObject curChild : children) {
                        if (!baseEntityId.equals(curChild.getCaseId())) {
                            baseEntityIds.add(curChild.getCaseId());
                        }
                    }

                    return baseEntityIds;
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(ArrayList<String> baseEntityIds) {
            super.onPostExecute(baseEntityIds);
            ArrayList<String> ids = new ArrayList<>();
            if (baseEntityIds != null) {
                ids = baseEntityIds;
            }

            Collections.reverse(ids);

            SiblingPicturesGroup siblingPicturesGroup = getActivity().findViewById(R.id.sibling_pictures);
            siblingPicturesGroup.setSiblingBaseEntityIds((BaseActivity) getActivity(), ids);
        }
    }

    private class SaveChildsStatusTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            showProgressDialog("Updating Child's Status", "");
        }

        @Override
        protected Void doInBackground(Void... params) {
            activateChildsStatus();

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            hideProgressDialog();
            super.onPostExecute(aVoid);
            updateViews();
        }
    }

    private class BCGNotificationDialog {

        private final SparseIntArray selectedOption = new SparseIntArray();
        private final int YES = 0;
        private final int NO = 1;
        private final int SELECTED_OPTION = 2;
        private final String[] singleChoiceItems = getResources().getStringArray(R.array.bcg_notification_options);
        private final int THEME = R.style.AppThemeAlertDialog;

        private AlertDialog alertDialog;
        private AlertDialog subDialogPositive;
        private AlertDialog subDialogNegative;

        private Context context;

        private final DialogInterface.OnClickListener backListener = new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                if (alertDialog != null) {

                    showDisablePositiveButton(alertDialog);
                    increaseBtnTextSizeTabletDevice(alertDialog);
                }
            }
        };

        private BCGNotificationDialog(final Context context, final DialogInterface.OnClickListener subDialogPositiveListener,
                                      final DialogInterface.OnClickListener subDialogNegativeListener) {
            this.context = context;

            alertDialog = new AlertDialog.Builder(context, THEME)
                    .setCustomTitle(View.inflate(context, R.layout.dialog_view_title_bcg_scar, null))
                    .setSingleChoiceItems(singleChoiceItems, -1, new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialogInterface, int selectedIndex) {

                            ((AlertDialog) dialogInterface).getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(true);
                            selectedOption.put(SELECTED_OPTION, selectedIndex);
                        }
                    })
                    .setPositiveButton(R.string.ok_button_label, new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialogInterface, int selectedIndex) {

                            alertDialog = new BCGNotificationDialog(context, subDialogPositiveListener, subDialogNegativeListener)
                                    .getAlertDialogInstance();

                            if (selectedOption.get(SELECTED_OPTION, NO) == YES) {
                                subDialogPositive.show();
                                increaseBtnTextSizeTabletDevice(subDialogPositive);
                            } else {
                                subDialogNegative.show();
                                increaseBtnTextSizeTabletDevice(subDialogNegative);
                            }
                        }
                    })
                    .setNegativeButton(R.string.dismiss_button_label, null)
                    .create();

            subDialogPositive = new AlertDialog.Builder(context, THEME)
                    .setCancelable(false)
                    .setCustomTitle(View.inflate(context, R.layout.dialog_view_title_bcg_turn_off, null))
                    .setPositiveButton(R.string.turn_off_reminder_button_label, subDialogPositiveListener)
                    .setNegativeButton(R.string.go_back_button_label, backListener)
                    .create();

            subDialogNegative = new AlertDialog.Builder(context, THEME)
                    .setCancelable(false)
                    .setTitle(R.string.create_reminder_label)
                    .setCustomTitle(View.inflate(context, R.layout.dialog_view_title_bcg_create, null))
                    .setPositiveButton(R.string.create_reminder_button_label, subDialogNegativeListener)
                    .setNegativeButton(R.string.go_back_button_label, backListener)
                    .create();
        }

        private void show() {
            showDisablePositiveButton(alertDialog);
            increaseBtnTextSizeTabletDevice(alertDialog);
        }

        private AlertDialog getAlertDialogInstance() {
            return alertDialog;
        }

        private void increaseBtnTextSizeTabletDevice(@NonNull AlertDialog alertDialog) {

            final float TEXT_SIZE = 20f;
            final int TABLET_WIDTH_DP = 600;
            final Button positiveButton = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
            final Button negativeButton = alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE);

            DisplayMetrics displayMetrics = new DisplayMetrics();
            final float DEVICE_DP = displayMetrics.density;
            ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            final int DEVICE_WIDTH_DP = (int) (displayMetrics.widthPixels / DEVICE_DP);
            final int DIALOG_BUTTON_PADDING_TOP = (int) getResources().getDimension(R.dimen.bcg_popup_button_padding_top);
            final int DEFAULT_DIALOG_BUTTON_PADDING = (int) getResources().getDimension(R.dimen.bcg_popup_button_padding);


            if (DEVICE_WIDTH_DP >= TABLET_WIDTH_DP) {

                positiveButton.setTextSize(TEXT_SIZE);
                negativeButton.setTextSize(TEXT_SIZE);
                positiveButton.setPadding(DEFAULT_DIALOG_BUTTON_PADDING, DIALOG_BUTTON_PADDING_TOP, DEFAULT_DIALOG_BUTTON_PADDING, DEFAULT_DIALOG_BUTTON_PADDING);
                negativeButton.setPadding(DEFAULT_DIALOG_BUTTON_PADDING, DIALOG_BUTTON_PADDING_TOP, DEFAULT_DIALOG_BUTTON_PADDING, DEFAULT_DIALOG_BUTTON_PADDING);
            }
        }

        public void showDisablePositiveButton(@NonNull AlertDialog alertDialog) {

            alertDialog.show();
            alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(false);
        }
    }

    public String getCurrentLocation() {
        return toolbar.getCurrentLocation();
    }


}
