package org.smartregister.child.provider;

import android.content.Context;
import android.database.Cursor;
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
import org.smartregister.AllConstants;
import org.smartregister.child.ChildLibrary;
import org.smartregister.child.R;
import org.smartregister.child.domain.RegisterActionParams;
import org.smartregister.child.domain.RepositoryHolder;
import org.smartregister.child.task.VaccinationAsyncTask;
import org.smartregister.child.task.WeightAsyncTask;
import org.smartregister.child.util.Constants;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.commonregistry.CommonRepository;
import org.smartregister.cursoradapter.RecyclerViewProvider;
import org.smartregister.growthmonitoring.repository.WeightRepository;
import org.smartregister.immunization.repository.VaccineRepository;
import org.smartregister.immunization.util.ImageUtils;
import org.smartregister.receiver.SyncStatusBroadcastReceiver;
import org.smartregister.service.AlertService;
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

/**
 * Created by ndegwamartin on 28/02/2019.
 */
public class ChildRegisterProvider implements RecyclerViewProvider<ChildRegisterProvider.RegisterViewHolder> {

    private final LayoutInflater inflater;
    private Set<org.smartregister.configurableviews.model.View> visibleColumns;

    private View.OnClickListener onClickListener;
    private View.OnClickListener paginationClickListener;

    private Context context;
    private CommonRepository commonRepository;
    private WeightRepository weightRepository;
    private VaccineRepository vaccineRepository;
    private AlertService alertService;

    public ChildRegisterProvider(Context context, RepositoryHolder repositoryHolder, Set visibleColumns, View.OnClickListener onClickListener, View.OnClickListener paginationClickListener, AlertService alertService) {

        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.visibleColumns = visibleColumns;

        this.onClickListener = onClickListener;
        this.paginationClickListener = paginationClickListener;

        this.context = context;
        this.commonRepository = repositoryHolder.getCommonRepository();
        this.weightRepository = repositoryHolder.getWeightRepository();
        this.vaccineRepository = repositoryHolder.getVaccineRepository();
        this.alertService = alertService;
    }

    public static void fillValue(TextView v, String value) {
        if (v != null)
            v.setText(value);

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
                MessageFormat.format(context.getString(R.string.str_page_info), currentPageCount,
                        totalPageCount));

        footerViewHolder.nextPageView.setVisibility(hasNext ? View.VISIBLE : View.INVISIBLE);
        footerViewHolder.previousPageView.setVisibility(hasPrevious ? View.VISIBLE : View.INVISIBLE);

        footerViewHolder.nextPageView.setOnClickListener(paginationClickListener);
        footerViewHolder.previousPageView.setOnClickListener(paginationClickListener);
    }

    private void populatePatientColumn(CommonPersonObjectClient pc, SmartRegisterClient client, final RegisterViewHolder viewHolder) {

        String firstName = Utils.getValue(pc.getColumnmaps(), Constants.KEY.FIRST_NAME, true);
        String lastName = Utils.getValue(pc.getColumnmaps(), Constants.KEY.LAST_NAME, true);
        String childName = Utils.getName(firstName, lastName);
        childName = childName + " " + Utils.getValue(pc.getColumnmaps(), Constants.KEY.NFC_CARD_IDENTIFIER, true);


        fillValue(viewHolder.childOpensrpID, Utils.getValue(pc.getColumnmaps(), Constants.KEY.ZEIR_ID, false));

        String motherFirstName = Utils.getValue(pc.getColumnmaps(), Constants.KEY.MOTHER_FIRST_NAME, true);
        if (StringUtils.isBlank(childName) && StringUtils.isNotBlank(motherFirstName)) {
            childName = "B/o " + motherFirstName.trim();
        }

        fillValue(viewHolder.patientName, WordUtils.capitalize(childName));

        String motherName = Utils.getValue(pc.getColumnmaps(), Constants.KEY.MOTHER_FIRST_NAME, true) + " " + Utils.getValue(pc, Constants.KEY.MOTHER_LAST_NAME, true);
        if (!StringUtils.isNotBlank(motherName)) {
            motherName = "M/G: " + motherName.trim();
        }


        fillValue(viewHolder.childMotherName, motherName);


        String durationString = "";
        String dobString = Utils.getValue(pc.getColumnmaps(), Constants.KEY.DOB, false);
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

        fillValue(viewHolder.childCardNumnber, Utils.getValue(pc.getColumnmaps(), Constants.KEY.EPI_CARD_NUMBER, false));


        String gender = Utils.getValue(pc.getColumnmaps(), AllConstants.ChildRegistrationFields.GENDER, true);

        final ImageView profilePic = viewHolder.imageView.findViewById(R.id.child_profilepic);
        renderProfileImage(pc.entityId(), gender, profilePic);

        attachGoToImmunizationPage(viewHolder.childProfileInfoLayout, client);


        View recordWeight = viewHolder.recordWeight;
        recordWeight.setBackground(context.getResources().getDrawable(R.drawable.record_weight_bg));
        recordWeight.setTag(client);
        recordWeight.setOnClickListener(onClickListener);
        recordWeight.setVisibility(View.INVISIBLE);
        recordWeight.setTag(R.id.record_action, Constants.RECORD_ACTION.WEIGHT);

        View recordVaccination = viewHolder.recordVaccination;
        recordVaccination.setTag(client);
        recordVaccination.setOnClickListener(onClickListener);
        recordVaccination.setVisibility(View.INVISIBLE);
        recordVaccination.setTag(R.id.record_action, Constants.RECORD_ACTION.VACCINATION);


        String lostToFollowUp = Utils.getValue(pc.getColumnmaps(), Constants.KEY.LOST_TO_FOLLOW_UP, false);
        String inactive = Utils.getValue(pc.getColumnmaps(), Constants.KEY.INACTIVE, false);

        if (show()) {
            try {

                RegisterActionParams params = new RegisterActionParams();
                params.setConvertView(viewHolder.registerColumns);
                params.setEntityId(pc.entityId());
                params.setLostToFollowUp(lostToFollowUp);
                params.setDobString(dobString);
                params.setInactive(inactive);
                params.setSmartRegisterClient(client);
                params.setUpdateOutOfCatchment(false);//TO DO update with dynamic parameter
                params.setOnClickListener(onClickListener);

                Utils.startAsyncTask(new WeightAsyncTask(params, commonRepository, weightRepository, context), null);
                Utils.startAsyncTask(new VaccinationAsyncTask(params, commonRepository, vaccineRepository, alertService, context), null);
            } catch (Exception e) {
                Log.e(getClass().getName(), e.getMessage(), e);
            }
        }

    }

    private void renderProfileImage(String entityId, String gender, ImageView profilePic) {
        int defaultImageResId = ImageUtils.profileImageResourceByGender(gender);
        profilePic.setImageResource(defaultImageResId);

        if (entityId != null && show()) { //image already in local storage most likely ):
            //set profile image by passing the client id.If the image doesn't exist in the image repository then download and save locally
            profilePic.setTag(R.id.entity_id, entityId);
            DrishtiApplication.getCachedImageLoaderInstance().getImageByClientId(entityId, OpenSRPImageLoader.getStaticImageListener(profilePic, 0, 0));
        }

    }

    private void attachGoToImmunizationPage(View view, SmartRegisterClient client) {
        view.setOnClickListener(onClickListener);
        view.setTag(client);
        view.setTag(R.id.record_action, Constants.RECORD_ACTION.NONE);
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

        if (ChildLibrary.getInstance().getProperties().hasProperty(Constants.PROPERTY.HOME_RECORD_WEIGHT_ENABLED)) {

            view.findViewById(R.id.record_weight_wrapper).setVisibility(ChildLibrary.getInstance().getProperties().getPropertyBoolean(Constants.PROPERTY.HOME_RECORD_WEIGHT_ENABLED) ? View.VISIBLE : View.GONE);
        }

        view.findViewById(R.id.child_next_appointment_wrapper).setVisibility(ChildLibrary.getInstance().getProperties().getPropertyBoolean(Constants.PROPERTY.HOME_NEXT_VISIT_DATE_ENABLED) ? View.VISIBLE : View.GONE);

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

    ////////////////////////////////////////////////////////////////
    // Inner classes
    ////////////////////////////////////////////////////////////////

    private boolean show() {

        return !ChildRegisterProvider.class.equals(this.getClass()) || !ChildLibrary.getInstance().context().allSharedPreferences().fetchIsSyncInitial() || !SyncStatusBroadcastReceiver.getInstance().isSyncing();

    }

    public static class RegisterViewHolder extends RecyclerView.ViewHolder {
        public TextView patientName;
        public TextView childOpensrpID;
        public TextView childMotherName;
        public TextView childAge;
        public TextView childCardNumnber;


        public View childProfileInfoLayout;

        public ImageView imageView;

        public View recordWeight;

        public View recordVaccination;

        public View registerColumns;

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

    public class FooterViewHolder extends RecyclerView.ViewHolder {
        public TextView pageInfoView;
        public Button nextPageView;
        public Button previousPageView;

        public FooterViewHolder(View view) {
            super(view);

            nextPageView = view.findViewById(R.id.btn_next_page);
            previousPageView = view.findViewById(R.id.btn_previous_page);
            pageInfoView = view.findViewById(R.id.txt_page_info);
        }
    }

}
