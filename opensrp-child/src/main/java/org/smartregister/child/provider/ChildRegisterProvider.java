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
import org.smartregister.child.R;
import org.smartregister.child.domain.RegisterActionParams;
import org.smartregister.child.domain.RepositoryHolder;
import org.smartregister.child.fragment.BaseChildRegisterFragment;
import org.smartregister.child.task.VaccinationAsyncTask;
import org.smartregister.child.task.WeightAsyncTask;
import org.smartregister.child.util.Constants;
import org.smartregister.child.util.DBConstants;
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

import static org.smartregister.util.Utils.getValue;

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
        String childName = Utils.getName(firstName, lastName);


        fillValue(viewHolder.childOpensrpID, getValue(pc.getColumnmaps(), DBConstants.KEY.ZEIR_ID, false));

        String motherFirstName = getValue(pc.getColumnmaps(), DBConstants.KEY.MOTHER_FIRST_NAME, true);
        if (StringUtils.isBlank(childName) && StringUtils.isNotBlank(motherFirstName)) {
            childName = "B/o " + motherFirstName.trim();
        }

        fillValue(viewHolder.patientName, WordUtils.capitalize(childName));

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


        String lostToFollowUp = getValue(pc.getColumnmaps(), DBConstants.KEY.LOST_TO_FOLLOW_UP, false);
        String inactive = getValue(pc.getColumnmaps(), DBConstants.KEY.INACTIVE, false);

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
            profilePic.setTag(org.smartregister.R.id.entity_id, entityId);
            DrishtiApplication.getCachedImageLoaderInstance().getImageByClientId(entityId, OpenSRPImageLoader.getStaticImageListener(profilePic, 0, 0));
        }

    }

    private void attachGoToImmunizationPage(View view, SmartRegisterClient client) {
        view.setOnClickListener(onClickListener);
        view.setTag(client);
        view.setTag(R.id.record_action, Constants.RECORD_ACTION.NONE);
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


    private boolean show() {

        return !ChildRegisterProvider.class.equals(this.getClass()) /*|| !allSharedPreferences.fetchIsSyncInitial()*/ || !SyncStatusBroadcastReceiver.getInstance().isSyncing();

    }


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

}
