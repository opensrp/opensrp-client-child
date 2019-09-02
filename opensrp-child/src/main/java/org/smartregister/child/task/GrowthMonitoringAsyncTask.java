package org.smartregister.child.task;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import org.joda.time.LocalDate;
import org.smartregister.child.R;
import org.smartregister.child.domain.RegisterActionParams;
import org.smartregister.child.util.Constants;
import org.smartregister.child.wrapper.GrowthMonitoringViewRecordUpdateWrapper;
import org.smartregister.commonregistry.CommonPersonObject;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.commonregistry.CommonRepository;
import org.smartregister.growthmonitoring.GrowthMonitoringLibrary;
import org.smartregister.growthmonitoring.domain.Height;
import org.smartregister.growthmonitoring.domain.Weight;
import org.smartregister.growthmonitoring.repository.HeightRepository;
import org.smartregister.growthmonitoring.repository.WeightRepository;
import org.smartregister.util.Utils;
import org.smartregister.view.contract.SmartRegisterClient;

import java.lang.ref.WeakReference;

import static org.smartregister.util.Utils.getValue;

/**
 * Created by ndegwamartin on 05/03/2019.
 */
public class GrowthMonitoringAsyncTask extends AsyncTask<Void, Void, Void> {
    private final WeakReference<View> convertView;
    private final String entityId;
    private final String lostToFollowUp;
    private final String inactive;
    private Weight weight;
    private Height height;
    private SmartRegisterClient client;
    private WeightRepository weightRepository;
    private HeightRepository heightRepository;
    private CommonRepository commonRepository;
    private WeakReference<Context> context;
    private Boolean updateOutOfCatchment;
    private View.OnClickListener onClickListener;
    private boolean hasProperty;
    private boolean monitorGrowth = false;

    public GrowthMonitoringAsyncTask(RegisterActionParams recordActionParams, CommonRepository commonRepository,
                                     WeightRepository weightRepository, HeightRepository heightRepository, Context context) {
        this.convertView = new WeakReference<>(recordActionParams.getConvertView());
        this.entityId = recordActionParams.getEntityId();
        this.lostToFollowUp = recordActionParams.getLostToFollowUp();
        this.inactive = recordActionParams.getInactive();
        this.client = recordActionParams.getSmartRegisterClient();
        this.updateOutOfCatchment = recordActionParams.getUpdateOutOfCatchment();
        this.onClickListener = recordActionParams.getOnClickListener();
        this.weightRepository = weightRepository;
        this.heightRepository = heightRepository;
        this.commonRepository = commonRepository;
        this.context = new WeakReference<>(context);

        hasProperty = GrowthMonitoringLibrary.getInstance().getAppProperties().hasProperty(org.smartregister.growthmonitoring.util.AppProperties.KEY.MONITOR_GROWTH);
        if (hasProperty) {
            monitorGrowth = GrowthMonitoringLibrary.getInstance().getAppProperties().getPropertyBoolean(org.smartregister.growthmonitoring.util.AppProperties.KEY.MONITOR_GROWTH);
        }
    }

    @Override
    protected Void doInBackground(Void... params) {
        weight = weightRepository.findUnSyncedByEntityId(entityId);
        String rawDob = ((CommonPersonObjectClient) this.client).getColumnmaps().get(Constants.KEY.DOB);
        LocalDate dob = new LocalDate(rawDob.contains("T") ? rawDob.substring(0, rawDob.indexOf('T')) : rawDob);
        weight = weight != null && new LocalDate(weight.getDate()).isEqual(dob) ? null : weight;
        if (hasProperty && monitorGrowth) {
            height = heightRepository.findUnSyncedByEntityId(entityId);
            height = height != null && new LocalDate(height.getDate()).isEqual(dob) ? null : height;
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void param) {
        GrowthMonitoringViewRecordUpdateWrapper wrapper = new GrowthMonitoringViewRecordUpdateWrapper();
        wrapper.setWeight(weight);
        if (hasProperty && monitorGrowth) {
            wrapper.setHeight(height);
        }
        wrapper.setLostToFollowUp(lostToFollowUp);
        wrapper.setInactive(inactive);
        wrapper.setClient(client);
        wrapper.setConvertView(convertView.get());
        updateRecordWeight(wrapper, updateOutOfCatchment);

    }


    private void updateRecordWeight(GrowthMonitoringViewRecordUpdateWrapper updateWrapper, Boolean updateOutOfCatchment) {

        View recordGrowth = updateWrapper.getConvertView().findViewById(R.id.record_growth);
        TextView recordGrowthText = updateWrapper.getConvertView().findViewById(R.id.record_growth_text);
        ImageView recordGrowthCheck = updateWrapper.getConvertView().findViewById(R.id.record_growth_check);
        recordGrowth.setVisibility(View.VISIBLE);

        if (updateWrapper.getWeight() != null || updateWrapper.getHeight() != null) {
            String weightString = "";
            if (updateWrapper.getWeight() != null) {
                weightString = Utils.kgStringSuffix(updateWrapper.getWeight().getKg());
            }
            String growthString;
            if (hasProperty && monitorGrowth) {
                String heightString = "";
                if (updateWrapper.getHeight() != null) {
                    heightString = Utils.cmStringSuffix(updateWrapper.getHeight().getCm());
                }

                growthString = heightString.isEmpty() ? weightString : weightString + ", " + heightString;
            } else {
                growthString = weightString;
            }

            recordGrowthText.setText(growthString);

            recordGrowthCheck.setVisibility(View.VISIBLE);
            recordGrowth.setClickable(false);
            recordGrowth.setBackground(new ColorDrawable(context.get().getResources().getColor(android.R.color.transparent)));
        } else {
            recordGrowthText.setText(context.get().getString(R.string.record_growth_with_nl));
            recordGrowthCheck.setVisibility(View.GONE);
            recordGrowth.setClickable(true);
        }

        // Update active/inactive/lostToFollowup status
        if (updateWrapper.getLostToFollowUp().equals(Boolean.TRUE.toString()) ||
                updateWrapper.getInactive().equals(Boolean.TRUE.toString())) {
            recordGrowth.setVisibility(View.INVISIBLE);
        }

        //Update Out of Catchment
        if (updateOutOfCatchment) {
            updateViews(updateWrapper.getConvertView(), updateWrapper.getClient());
        }
    }

    protected void updateViews(View catchmentView, SmartRegisterClient client) {
        CommonPersonObjectClient pc = (CommonPersonObjectClient) client;

        if (commonRepository != null) {
            CommonPersonObject commonPersonObject = commonRepository.findByBaseEntityId(pc.entityId());

            View recordVaccination = catchmentView.findViewById(R.id.record_vaccination);
            recordVaccination.setVisibility(View.VISIBLE);

            View moveToCatchment = catchmentView.findViewById(R.id.move_to_catchment);
            moveToCatchment.setVisibility(View.GONE);

            if (commonPersonObject == null) { //Out of area -- doesn't exist in local database

                catchmentView.findViewById(R.id.child_profile_info_layout).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Utils.showShortToast(context.get(), context.get().getString(R.string.show_vaccine_card_disabled));
                    }
                });

                TextView recordWeightText = catchmentView.findViewById(R.id.record_growth_text);
                recordWeightText.setText(R.string.record_service);

                String openSrpId = getValue(pc.getColumnmaps(), Constants.KEY.ZEIR_ID, false);

                View recordWeight = catchmentView.findViewById(R.id.record_growth);
                recordWeight.setBackground(context.get().getResources().getDrawable(R.drawable.record_growth_bg));
                recordWeight.setTag(openSrpId);
                recordWeight.setClickable(true);
                recordWeight.setEnabled(true);
                recordWeight.setOnClickListener(onClickListener);

            }

        }
    }

}

