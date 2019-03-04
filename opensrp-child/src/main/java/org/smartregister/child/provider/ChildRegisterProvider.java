package org.smartregister.child.provider;

import android.content.Context;
import android.database.Cursor;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.WordUtils;
import org.joda.time.DateTime;
import org.smartregister.child.R;
import org.smartregister.child.fragment.BaseChildRegisterFragment;
import org.smartregister.child.util.DBConstants;
import org.smartregister.child.wrapper.WeightViewRecordUpdateWrapper;
import org.smartregister.commonregistry.CommonPersonObject;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.commonregistry.CommonRepository;
import org.smartregister.cursoradapter.RecyclerViewProvider;
import org.smartregister.immunization.util.ImageUtils;
import org.smartregister.util.DateUtil;
import org.smartregister.util.OpenSRPImageLoader;
import org.smartregister.util.Utils;
import org.smartregister.view.activity.DrishtiApplication;
import org.smartregister.view.contract.SmartRegisterClient;
import org.smartregister.view.contract.SmartRegisterClients;
import org.smartregister.view.dialog.FilterOption;
import org.smartregister.view.dialog.ServiceModeOption;
import org.smartregister.view.dialog.SortOption;
import org.smartregister.view.viewholder.OnClickFormLauncher;

import java.text.MessageFormat;
import java.util.Set;

import static org.smartregister.util.Utils.getValue;

/**
 * Created by ndegwamartin on 28/02/2019.
 */
public class ChildRegisterProvider implements RecyclerViewProvider<ChildRegisterProvider.RegisterViewHolder> {

    public final static String LINE_SEPARATOR = System.getProperty("line.separator");

    private final LayoutInflater inflater;
    private Set<org.smartregister.configurableviews.model.View> visibleColumns;

    private View.OnClickListener onClickListener;
    private View.OnClickListener paginationClickListener;

    private Context context;
    private CommonRepository commonRepository;

    public ChildRegisterProvider(Context context, CommonRepository commonRepository, Set visibleColumns, View.OnClickListener onClickListener, View.OnClickListener paginationClickListener) {

        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.visibleColumns = visibleColumns;

        this.onClickListener = onClickListener;
        this.paginationClickListener = paginationClickListener;

        this.context = context;
        this.commonRepository = commonRepository;
    }

    @Override
    public void getView(Cursor cursor, SmartRegisterClient client, RegisterViewHolder viewHolder) {
        CommonPersonObjectClient pc = (CommonPersonObjectClient) client;
        if (visibleColumns.isEmpty()) {
            populatePatientColumn(pc, client, viewHolder);
           // populateLastColumn(pc, viewHolder);

            return;
        }
    }

    @Override
    public void getFooterView(RecyclerView.ViewHolder viewHolder, int currentPageCount, int totalPageCount, boolean hasNext, boolean hasPrevious) {
        FooterViewHolder footerViewHolder = (FooterViewHolder) viewHolder;
        footerViewHolder.pageInfoView.setText(
                MessageFormat.format(context.getString(org.smartregister.R.string.str_page_info), currentPageCount,
                        totalPageCount));

        footerViewHolder.nextPageView.setVisibility(hasNext ? View.VISIBLE : View.INVISIBLE);
        footerViewHolder.previousPageView.setVisibility(hasPrevious ? View.VISIBLE : View.INVISIBLE);

        footerViewHolder.nextPageView.setOnClickListener(paginationClickListener);
        footerViewHolder.previousPageView.setOnClickListener(paginationClickListener);
    }

    private void populatePatientColumn(CommonPersonObjectClient pc, SmartRegisterClient client, final RegisterViewHolder viewHolder) {

        String firstName = Utils.getValue(pc.getColumnmaps(), DBConstants.KEY.FIRST_NAME, true);
        String lastName = Utils.getValue(pc.getColumnmaps(), DBConstants.KEY.LAST_NAME, true);
        String patientName = Utils.getName(firstName, lastName);


        String motherFirstName = getValue(pc.getColumnmaps(), DBConstants.KEY.MOTHER_FIRST_NAME, true);
        if (StringUtils.isBlank(patientName) && StringUtils.isNotBlank(motherFirstName)) {
            patientName = "B/o " + motherFirstName.trim();
        }

        fillValue(viewHolder.patientName, WordUtils.capitalize(patientName));

        String motherName = getValue(pc.getColumnmaps(), DBConstants.KEY.MOTHER_FIRST_NAME, true) + " " + getValue(pc, DBConstants.KEY.MOTHER_LAST_NAME, true);
        if (!StringUtils.isNotBlank(motherName)) {
            motherName = "M/G: " + motherName.trim();
        }


        fillValue(viewHolder.childMotherName, motherName);


        String durationString = "";
        String dobString = getValue(pc.getColumnmaps(), DBConstants.KEY.DOB, false);
        DateTime birthDateTime = Utils.dobStringToDateTime(dobString);
        if (birthDateTime != null) {
            try {
                String duration = DateUtil.getDuration(birthDateTime);
                if (duration != null) {
                    durationString = duration;
                }
            } catch (Exception e) {
                Log.e(getClass().getName(), e.toString(), e);
            }
        }
        fillValue(viewHolder.childAge, durationString);

        fillValue(viewHolder.childCardNumnber, Utils.getValue(pc.getColumnmaps(), DBConstants.KEY.EPI_CARD_NUMBER, false));


        String gender = getValue(pc.getColumnmaps(), DBConstants.KEY.GENDER, true);

        final ImageView profilePic = viewHolder.imageView.findViewById(R.id.child_profilepic);
        int defaultImageResId = ImageUtils.profileImageResourceByGender(gender);
        profilePic.setImageResource(defaultImageResId);

        if (pc.entityId() != null && show()) { //image already in local storage most likely ):
            //set profile image by passing the client id.If the image doesn't exist in the image repository then download and save locally
            profilePic.setTag(org.smartregister.R.id.entity_id, pc.entityId());
            DrishtiApplication.getCachedImageLoaderInstance().getImageByClientId(pc.entityId(), OpenSRPImageLoader.getStaticImageListener(profilePic, 0, 0));
        }

        viewHolder.childProfileInfoLayout.setTag(client);
        viewHolder.childProfileInfoLayout.setOnClickListener(onClickListener);


        String villageTown = Utils.getValue(pc.getColumnmaps(), DBConstants.KEY.ID, true);
        fillValue((viewHolder.childOpensrpID), villageTown);

        View patient = viewHolder.patientColumn;
        // attachPatientOnclickListener(patient, client);

        View dueButton = viewHolder.dueButton;
//        attachDosageOnclickListener(dueButton, client);


        View recordWeight = viewHolder.recordWeight;
        recordWeight.setBackground(context.getResources().getDrawable(R.drawable.record_weight_bg));
        recordWeight.setTag(client);
        recordWeight.setOnClickListener(onClickListener);
        recordWeight.setVisibility(View.INVISIBLE);

        View recordVaccination = viewHolder.recordVaccination;
        recordVaccination.setTag(client);
        recordVaccination.setOnClickListener(onClickListener);
        recordVaccination.setVisibility(View.INVISIBLE);

        String lostToFollowUp = getValue(pc.getColumnmaps(), DBConstants.KEY.LOST_TO_FOLLOW_UP, false);
        String inactive = getValue(pc.getColumnmaps(), DBConstants.KEY.INACTIVE, false);

        if (show()) {
            try {
                //   Utils.startAsyncTask(new WeightAsyncTask(convertView, pc.entityId(), lostToFollowUp, inactive, client, cursor), null);
                //    Utils.startAsyncTask(new VaccinationAsyncTask(convertView, pc.entityId(), dobString, lostToFollowUp, inactive, client, cursor), null);
            } catch (Exception e) {
                Log.e(getClass().getName(), e.getMessage(), e);
            }
        }


        viewHolder.registerColumns.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewHolder.patientColumn.performClick();
            }
        });

        viewHolder.recordWeight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewHolder.dueButton.performClick();
            }
        });
    }

    private void populateLastColumn(CommonPersonObjectClient pc, RegisterViewHolder viewHolder) {
        if (commonRepository != null) {
            CommonPersonObject commonPersonObject = commonRepository.findByBaseEntityId(pc.entityId());
            if (commonPersonObject != null) {
                viewHolder.dueButton.setVisibility(View.VISIBLE);
                viewHolder.dueButton.setText("Home Visit");
                viewHolder.dueButton.setAllCaps(true);
            } else {
                viewHolder.dueButton.setVisibility(View.GONE);
            }
        }
    }

    private void attachPatientOnclickListener(View view, SmartRegisterClient client) {
        view.setOnClickListener(onClickListener);
        view.setTag(client);
        view.setTag(R.id.VIEW_ID, BaseChildRegisterFragment.CLICK_VIEW_NORMAL);
    }

    private void attachDosageOnclickListener(View view, SmartRegisterClient client) {
        view.setOnClickListener(onClickListener);
        view.setTag(client);
        view.setTag(R.id.VIEW_ID, BaseChildRegisterFragment.CLICK_VIEW_DOSAGE_STATUS);
    }

    @Override
    public SmartRegisterClients updateClients(FilterOption villageFilter, ServiceModeOption serviceModeOption, FilterOption searchFilter, SortOption sortOption) {
        return null;
    }

    @Override
    public void onServiceModeSelected(ServiceModeOption serviceModeOption) {//Implement Abstract Method
    }

    @Override
    public OnClickFormLauncher newFormLauncher(String formName, String entityId, String metaData) {
        return null;
    }

    @Override
    public LayoutInflater inflater() {
        return inflater;
    }

    @Override
    public RegisterViewHolder createViewHolder(ViewGroup parent) {
        View view = inflater.inflate(R.layout.child_register_list_row, parent, false);

        /*
        ConfigurableViewsHelper helper = ConfigurableViewsLibrary.getInstance().getConfigurableViewsHelper();
        if (helper.isJsonViewsEnabled()) {

            ViewConfiguration viewConfiguration = helper.getViewConfiguration(Constants.CONFIGURATION.HOME_REGISTER_ROW);
            ViewConfiguration commonConfiguration = helper.getViewConfiguration(COMMON_REGISTER_ROW);

            if (viewConfiguration != null) {
                return helper.inflateDynamicView(viewConfiguration, commonConfiguration, view, R.id.register_columns, false);
            }
        }*/

        return new RegisterViewHolder(view);
    }

    @Override
    public RecyclerView.ViewHolder createFooterHolder(ViewGroup parent) {
        View view = inflater.inflate(R.layout.smart_register_pagination, parent, false);
        return new FooterViewHolder(view);
    }

    @Override
    public boolean isFooterViewHolder(RecyclerView.ViewHolder viewHolder) {
        return FooterViewHolder.class.isInstance(viewHolder);
    }


    public static void fillValue(TextView v, String value) {
        if (v != null)
            v.setText(value);

    }

    ////////////////////////////////////////////////////////////////
    // Inner classes
    ////////////////////////////////////////////////////////////////

    public class RegisterViewHolder extends RecyclerView.ViewHolder {
        public TextView patientName;
        public TextView childOpensrpID;
        public TextView childMotherName;
        public TextView childAge;
        public TextView childCardNumnber;


        public Button dueButton;
        public View patientColumn;
        public View childProfileInfoLayout;

        public ImageView imageView;

        public View registerColumns;

        public View recordWeight;

        public View recordVaccination;

        public RegisterViewHolder(View itemView) {
            super(itemView);

            patientName = itemView.findViewById(R.id.child_name);

            childOpensrpID = itemView.findViewById(R.id.child_zeir_id);

            childMotherName = itemView.findViewById(R.id.child_mothername);

            childAge = itemView.findViewById(R.id.child_age);

            childCardNumnber = itemView.findViewById(R.id.child_card_number);

            imageView = itemView.findViewById(R.id.child_profilepic);

            childProfileInfoLayout = itemView.findViewById(R.id.child_profile_info_layout);

            recordWeight = itemView.findViewById(R.id.record_weight);

            recordVaccination = itemView.findViewById(R.id.record_vaccination);


            registerColumns = itemView.findViewById(R.id.register_columns);

        }
    }


    private boolean show() {
        //return !ChildSmartClientsProvider.class.equals(this.getClass()) || !allSharedPreferences.fetchIsSyncInitial() || !SyncStatusBroadcastReceiver.getInstance().isSyncing();
        return true;
    }


    private void updateRecordWeight(WeightViewRecordUpdateWrapper updateWrapper) {

        View recordWeight = updateWrapper.getConvertView().findViewById(R.id.record_weight);
        recordWeight.setVisibility(View.VISIBLE);

        if (updateWrapper.getWeight() != null) {
            TextView recordWeightText = updateWrapper.getConvertView().findViewById(R.id.record_weight_text);
            recordWeightText.setText(Utils.kgStringSuffix(updateWrapper.getWeight().getKg()));

            ImageView recordWeightCheck = updateWrapper.getConvertView().findViewById(R.id.record_weight_check);
            recordWeightCheck.setVisibility(View.VISIBLE);

            recordWeight.setClickable(false);
            recordWeight.setBackground(new ColorDrawable(context.getResources()
                    .getColor(android.R.color.transparent)));
        } else {
            TextView recordWeightText = updateWrapper.getConvertView().findViewById(R.id.record_weight_text);
            recordWeightText.setText(context.getString(R.string.record_weight_with_nl));

            ImageView recordWeightCheck = updateWrapper.getConvertView().findViewById(R.id.record_weight_check);
            recordWeightCheck.setVisibility(View.GONE);
            recordWeight.setClickable(true);
        }

        // Update active/inactive/lostToFollowup status
        if (updateWrapper.getLostToFollowUp().equals(Boolean.TRUE.toString()) || updateWrapper.getInactive().equals(Boolean.TRUE.toString())) {
            recordWeight.setVisibility(View.INVISIBLE);
        }

        //Update Out of Catchment
        //  if (updateWrapper.getCursor() instanceof AdvancedSearchFragment.AdvancedMatrixCursor) {
        //      updateViews(updateWrapper.getConvertView(), updateWrapper.getClient(), true);
        // }
    }

    /*
        private void updateRecordVaccination(VaccineViewRecordUpdateWrapper updateWrapper) {
            View recordVaccination = updateWrapper.getConvertView().findViewById(R.id.record_vaccination);
            recordVaccination.setVisibility(View.VISIBLE);

            TextView recordVaccinationText =  updateWrapper.getConvertView().findViewById(R.id.record_vaccination_text);
            ImageView recordVaccinationCheck =  updateWrapper.getConvertView().findViewById(R.id.record_vaccination_check);
            recordVaccinationCheck.setVisibility(View.GONE);

            updateWrapper.getConvertView().setLayoutParams(clientViewLayoutParams);

            State state = State.WAITING;
            String stateKey = "";

            Map<String, Object> nv = updateWrapper.getNv();

            if (nv != null) {
                if (nv.get(DBConstants.KEY.VACCINE) != null && nv.get(DBConstants.KEY.VACCINE) instanceof VaccineRepo.Vaccine) {
                    VaccineRepo.Vaccine vaccine = (VaccineRepo.Vaccine) nv.get(DBConstants.KEY.VACCINE);
                    stateKey = VaccinateActionUtils.stateKey(vaccine);
                }

                Alert alert = null;
                if (nv.get(DBConstants.KEY.ALERT) != null && nv.get(DBConstants.KEY.ALERT) instanceof Alert) {
                    alert = (Alert) nv.get(DBConstants.KEY.ALERT);
                }

                if (alert == null) {
                    state = State.NO_ALERT;
                } else if (AlertStatus.normal.equals(alert.status())) {
                    state = State.DUE;
                } else if (AlertStatus.upcoming.equals(alert.status())) {
                    Calendar today = Calendar.getInstance();
                    today.set(Calendar.HOUR_OF_DAY, 0);
                    today.set(Calendar.MINUTE, 0);
                    today.set(Calendar.SECOND, 0);
                    today.set(Calendar.MILLISECOND, 0);

                    DateTime dueDate = null;
                    if (nv.get(DBConstants.KEY.DATE) != null && nv.get(DBConstants.KEY.DATE) instanceof DateTime) {
                        dueDate = (DateTime) nv.get(DBConstants.KEY.DATE);
                    }

                    if (dueDate != null && dueDate.getMillis() >= (today.getTimeInMillis() + TimeUnit.MILLISECONDS.convert(1, TimeUnit.DAYS)) && dueDate.getMillis() < (today.getTimeInMillis() + TimeUnit.MILLISECONDS.convert(7, TimeUnit.DAYS))) {
                        state = State.UPCOMING_NEXT_7_DAYS;
                    } else {
                        state = State.UPCOMING;
                    }
                } else if (AlertStatus.urgent.equals(alert.status())) {
                    state = State.OVERDUE;
                } else if (AlertStatus.expired.equals(alert.status())) {
                    state = State.EXPIRED;
                }
            }

            // Check for fully immunized child
            if (nv == null && updateWrapper.getVaccines() != null && !updateWrapper.getVaccines().isEmpty()) {
                state = State.FULLY_IMMUNIZED;
            }

            // Update active/inactive/lostToFollowup status
            if (updateWrapper.getLostToFollowUp().equals(Boolean.TRUE.toString())) {
                state = State.LOST_TO_FOLLOW_UP;
            }

            if (updateWrapper.getInactive().equals(Boolean.TRUE.toString())) {
                state = State.INACTIVE;
            }

            if (state.equals(State.FULLY_IMMUNIZED)) {
                recordVaccinationText.setText(R.string.fully_immunized_label);
                recordVaccinationText.setTextColor(context.getResources().getColor(R.color.client_list_grey));

                recordVaccinationCheck.setImageResource(R.drawable.ic_action_check);
                recordVaccinationCheck.setVisibility(View.VISIBLE);

                recordVaccination.setBackgroundColor(context.getResources().getColor(R.color.white));
                recordVaccination.setEnabled(false);

            } else if (state.equals(State.INACTIVE)) {
                recordVaccinationText.setText(R.string.inactive);
                recordVaccinationText.setTextColor(context.getResources().getColor(R.color.client_list_grey));

                recordVaccinationCheck.setImageResource(R.drawable.ic_icon_status_inactive);
                recordVaccinationCheck.setVisibility(View.VISIBLE);

                recordVaccination.setBackgroundColor(context.getResources().getColor(R.color.white));
                recordVaccination.setEnabled(false);


            } else if (state.equals(State.LOST_TO_FOLLOW_UP)) {
                recordVaccinationText.setText(R.string.lost_to_follow_up_with_nl);
                recordVaccinationText.setTextColor(context.getResources().getColor(R.color.client_list_grey));

                recordVaccinationCheck.setImageResource(R.drawable.ic_icon_status_losttofollowup);
                recordVaccinationCheck.setVisibility(View.VISIBLE);

                recordVaccination.setBackgroundColor(context.getResources().getColor(R.color.white));
                recordVaccination.setEnabled(false);

            } else if (state.equals(State.WAITING)) {
                recordVaccinationText.setText(R.string.waiting_label);
                recordVaccinationText.setTextColor(context.getResources().getColor(R.color.client_list_grey));

                recordVaccination.setBackgroundColor(context.getResources().getColor(R.color.white));
                recordVaccination.setEnabled(false);
            } else if (state.equals(State.EXPIRED)) {
                recordVaccinationText.setText(R.string.expired_label);
                recordVaccinationText.setTextColor(context.getResources().getColor(R.color.client_list_grey));

                recordVaccination.setBackgroundColor(context.getResources().getColor(R.color.white));
                recordVaccination.setEnabled(false);
            } else if (state.equals(State.UPCOMING)) {
                recordVaccinationText.setText(context.getString(R.string.upcoming_label) + LINE_SEPARATOR + stateKey);
                recordVaccinationText.setTextColor(context.getResources().getColor(R.color.client_list_grey));

                recordVaccination.setBackgroundColor(context.getResources().getColor(R.color.white));
                recordVaccination.setEnabled(false);
            } else if (state.equals(State.UPCOMING_NEXT_7_DAYS)) {
                recordVaccinationText.setText(context.getString(R.string.upcoming_label) + LINE_SEPARATOR + stateKey);
                recordVaccinationText.setTextColor(context.getResources().getColor(R.color.client_list_grey));

                recordVaccination.setBackground(context.getResources().getDrawable(R.drawable.due_vaccine_light_blue_bg));
                recordVaccination.setEnabled(true);
            } else if (state.equals(State.DUE)) {
                recordVaccinationText.setText(context.getString(R.string.record_label) + LINE_SEPARATOR + stateKey);
                recordVaccinationText.setTextColor(context.getResources().getColor(R.color.status_bar_text_almost_white));

                recordVaccination.setBackground(context.getResources().getDrawable(R.drawable.due_vaccine_blue_bg));
                recordVaccination.setEnabled(true);
            } else if (state.equals(State.OVERDUE)) {
                recordVaccinationText.setText(context.getString(R.string.record_label) + LINE_SEPARATOR + stateKey);
                recordVaccinationText.setTextColor(context.getResources().getColor(R.color.status_bar_text_almost_white));

                recordVaccination.setBackground(context.getResources().getDrawable(R.drawable.due_vaccine_red_bg));
                recordVaccination.setEnabled(true);
            } else if (state.equals(State.NO_ALERT)) {
                if (StringUtils.isNotBlank(stateKey) && (StringUtils.containsIgnoreCase(stateKey, Constants.KEY.WEEK) || StringUtils.containsIgnoreCase(stateKey, Constants.KEY.MONTH)) && !updateWrapper.getVaccines().isEmpty()) {
                    Vaccine vaccine = updateWrapper.getVaccines().isEmpty() ? null : updateWrapper.getVaccines().get(updateWrapper.getVaccines().size() - 1);
                    String previousStateKey = VaccinateActionUtils.previousStateKey(Constants.KEY.CHILD, vaccine);
                    if (previousStateKey != null) {
                        recordVaccinationText.setText(previousStateKey);
                    } else {
                        recordVaccinationText.setText(stateKey);
                    }
                    recordVaccinationCheck.setImageResource(R.drawable.ic_action_check);
                    recordVaccinationCheck.setVisibility(View.VISIBLE);
                } else {
                    recordVaccinationText.setText(context.getString(R.string.upcoming_label) + LINE_SEPARATOR + stateKey);
                }
                recordVaccinationText.setTextColor(context.getResources().getColor(R.color.client_list_grey));

                recordVaccination.setBackgroundColor(context.getResources().getColor(R.color.white));
                recordVaccination.setEnabled(false);
            } else {
                recordVaccinationText.setText("");
                recordVaccinationText.setTextColor(context.getResources().getColor(R.color.client_list_grey));

                recordVaccination.setBackgroundColor(context.getResources().getColor(R.color.white));
                recordVaccination.setEnabled(false);
            }

            //Update Out of Catchment
          //  if (updateWrapper.getCursor() instanceof AdvancedSearchFragment.AdvancedMatrixCursor) {
            //    updateViews(updateWrapper.getConvertView(), updateWrapper.getClient(), false);
           // }
        }
    /*
        private void updateViews(View convertView, SmartRegisterClient client, boolean isWeightRecord) {

            CommonPersonObjectClient pc = (CommonPersonObjectClient) client;

            if (commonRepository != null) {
                CommonPersonObject commonPersonObject = commonRepository.findByBaseEntityId(pc.entityId());

                View recordVaccination = convertView.findViewById(R.id.record_vaccination);
                recordVaccination.setVisibility(View.VISIBLE);

                View moveToCatchment = convertView.findViewById(R.id.move_to_catchment);
                moveToCatchment.setVisibility(View.GONE);

                if (commonPersonObject == null) { //Out of area -- doesn't exist in local database

                    convertView.findViewById(R.id.child_profile_info_layout).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Toast.makeText(context, context.getString(R.string.show_vaccine_card_disabled), Toast.LENGTH_SHORT).show();
                        }
                    });

                    if (isWeightRecord) {
                        TextView recordWeightText = convertView.findViewById(R.id.record_weight_text);
                        recordWeightText.setText("Record\nservice");

                        String zeirId = getValue(pc.getColumnmaps(), DBConstants.KEY.ZEIR_ID, false);

                        View recordWeight = convertView.findViewById(R.id.record_weight);
                        recordWeight.setBackground(context.getResources().getDrawable(R.drawable.record_weight_bg));
                        recordWeight.setTag(zeirId);
                        recordWeight.setClickable(true);
                        recordWeight.setEnabled(true);
                        recordWeight.setOnClickListener(onClickListener);
                    } else {

                        TextView moveToCatchmentText =  convertView.findViewById(R.id.move_to_catchment_text);
                        moveToCatchmentText.setText("Move to my\ncatchment");

                        String motherBaseEntityId = getValue(pc.getColumnmaps(), DBConstants.KEY.MOTHER_BASE_ENTITY_ID, false);
                        String entityId = pc.entityId();

                        List<String> ids = new ArrayList<>();
                        ids.add(motherBaseEntityId);
                        ids.add(entityId);

                        moveToCatchment.setBackground(context.getResources().getDrawable(R.drawable.record_weight_bg));
                        moveToCatchment.setTag(ids);
                        moveToCatchment.setClickable(true);
                        moveToCatchment.setEnabled(true);
                        moveToCatchment.setOnClickListener(onClickListener);

                        moveToCatchment.setVisibility(View.VISIBLE);
                        recordVaccination.setVisibility(View.GONE);
                    }
                }

            }
        }

    */
    public class FooterViewHolder extends RecyclerView.ViewHolder {
        public TextView pageInfoView;
        public Button nextPageView;
        public Button previousPageView;

        public FooterViewHolder(View view) {
            super(view);

            nextPageView = view.findViewById(org.smartregister.R.id.btn_next_page);
            previousPageView = view.findViewById(org.smartregister.R.id.btn_previous_page);
            pageInfoView = view.findViewById(org.smartregister.R.id.txt_page_info);
        }
    }

    private enum State {
        DUE,
        OVERDUE,
        UPCOMING_NEXT_7_DAYS,
        UPCOMING,
        INACTIVE,
        LOST_TO_FOLLOW_UP,
        EXPIRED,
        WAITING,
        NO_ALERT,
        FULLY_IMMUNIZED
    }
}
/*
    private class WeightAsyncTask extends AsyncTask<Void, Void, Void> {
        private final View convertView;
        private final String entityId;
        private final String lostToFollowUp;
        private final String inactive;
        private Weight weight;
        private SmartRegisterClient client;
        private Cursor cursor;

        private WeightAsyncTask(View convertView,
                                String entityId,
                                String lostToFollowUp,
                                String inactive,
                                SmartRegisterClient smartRegisterClient,
                                Cursor cursor) {
            this.convertView = convertView;
            this.entityId = entityId;
            this.lostToFollowUp = lostToFollowUp;
            this.inactive = inactive;
            this.client = smartRegisterClient;
            this.cursor = cursor;
        }


        @Override
        protected Void doInBackground(Void... params) {
            weight = weightRepository.findUnSyncedByEntityId(entityId);
            return null;
        }

        @Override
        protected void onPostExecute(Void param) {
            WeightViewRecordUpdateWrapper wrapper = new WeightViewRecordUpdateWrapper();
            wrapper.setWeight(weight);
            wrapper.setLostToFollowUp(lostToFollowUp);
            wrapper.setInactive(inactive);
            wrapper.setClient(client);
            wrapper.setCursor(cursor);
            wrapper.setConvertView(convertView);
            updateRecordWeight(wrapper);

        }
    }

    private class VaccinationAsyncTask extends AsyncTask<Void, Void, Void> {
        private final View convertView;
        private final String entityId;
        private final String dobString;
        private final String lostToFollowUp;
        private final String inactive;
        private List<Vaccine> vaccines = new ArrayList<>();
        private SmartRegisterClient client;
        private Cursor cursor;
        private Map<String, Object> nv = null;

        private VaccinationAsyncTask(View convertView,
                                     String entityId,
                                     String dobString,
                                     String lostToFollowUp,
                                     String inactive,
                                     SmartRegisterClient smartRegisterClient,
                                     Cursor cursor) {
            this.convertView = convertView;
            this.entityId = entityId;
            this.dobString = dobString;
            this.lostToFollowUp = lostToFollowUp;
            this.inactive = inactive;
            this.client = smartRegisterClient;
            this.cursor = cursor;
        }


        @Override
        protected Void doInBackground(Void... params) {
            vaccines = vaccineRepository.findByEntityId(entityId);
            List<Alert> alerts = alertService.findByEntityId(entityId);

            Map<String, Date> recievedVaccines = receivedVaccines(vaccines);

            DateTime dateTime = util.Utils.dobStringToDateTime(dobString);
            List<Map<String, Object>> sch = generateScheduleList(DBConstants.KEY.CHILD, dateTime, recievedVaccines, alerts);

            if (vaccines.isEmpty()) {
                List<VaccineRepo.Vaccine> vList = Arrays.asList(VaccineRepo.Vaccine.values());
                nv = nextVaccineDue(sch, vList);
            }

            if (nv == null) {
                Date lastVaccine = null;
                if (!vaccines.isEmpty()) {
                    Vaccine vaccine = vaccines.get(vaccines.size() - 1);
                    lastVaccine = vaccine.getDate();
                }

                nv = nextVaccineDue(sch, lastVaccine);
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void param) {

            VaccineViewRecordUpdateWrapper wrapper = new VaccineViewRecordUpdateWrapper();
            wrapper.setVaccines(vaccines);
            wrapper.setLostToFollowUp(lostToFollowUp);
            wrapper.setInactive(inactive);
            wrapper.setClient(client);
            wrapper.setCursor(cursor);
            wrapper.setConvertView(convertView);
            wrapper.setNv(nv);
            updateRecordVaccination(wrapper);

        }
    }

}
*/