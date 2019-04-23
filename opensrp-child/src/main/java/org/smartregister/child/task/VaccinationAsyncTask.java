package org.smartregister.child.task;

import android.content.Context;
import android.os.AsyncTask;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.smartregister.child.R;
import org.smartregister.child.domain.RegisterActionParams;
import org.smartregister.child.util.Constants;
import org.smartregister.child.util.DBConstants;
import org.smartregister.child.wrapper.VaccineViewRecordUpdateWrapper;
import org.smartregister.commonregistry.CommonPersonObject;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.commonregistry.CommonRepository;
import org.smartregister.domain.Alert;
import org.smartregister.domain.AlertStatus;
import org.smartregister.immunization.db.VaccineRepo;
import org.smartregister.immunization.domain.Vaccine;
import org.smartregister.immunization.repository.VaccineRepository;
import org.smartregister.immunization.util.VaccinateActionUtils;
import org.smartregister.immunization.util.VaccinatorUtils;
import org.smartregister.service.AlertService;
import org.smartregister.util.Utils;
import org.smartregister.view.contract.SmartRegisterClient;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static org.smartregister.immunization.util.VaccinatorUtils.nextVaccineDue;
import static org.smartregister.immunization.util.VaccinatorUtils.receivedVaccines;
import static org.smartregister.util.Utils.getValue;

/**
 * Created by ndegwamartin on 05/03/2019.
 */
public class VaccinationAsyncTask extends AsyncTask<Void, Void, Void> {

    public final static String LINE_SEPARATOR = System.getProperty("line.separator");

    private final View convertView;
    private final String entityId;
    private final String dobString;
    private final String lostToFollowUp;
    private final String inactive;
    private List<Vaccine> vaccines = new ArrayList<>();
    private SmartRegisterClient client;
    private Map<String, Object> nv = null;
    private VaccineRepository vaccineRepository;
    private CommonRepository commonRepository;
    private Context context;
    private Boolean updateOutOfCatchment;
    private View.OnClickListener onClickListener;
    private AlertService alertService;


    public VaccinationAsyncTask(RegisterActionParams recordActionParams, CommonRepository commonRepository, VaccineRepository vaccineRepository, AlertService alertService, Context context) {

        this.convertView = recordActionParams.getConvertView();
        this.entityId = recordActionParams.getEntityId();
        this.dobString = recordActionParams.getDobString();
        this.lostToFollowUp = recordActionParams.getLostToFollowUp();
        this.inactive = recordActionParams.getInactive();
        this.client = recordActionParams.getSmartRegisterClient();
        this.updateOutOfCatchment = recordActionParams.getUpdateOutOfCatchment();
        this.onClickListener = recordActionParams.getOnClickListener();
        this.vaccineRepository = vaccineRepository;
        this.commonRepository = commonRepository;
        this.context = context;
        this.alertService = alertService;


    }


    @Override
    protected Void doInBackground(Void... params) {
        vaccines = vaccineRepository.findByEntityId(entityId);
        List<Alert> alerts = alertService.findByEntityId(entityId);

        Map<String, Date> receivedVaccines = receivedVaccines(vaccines);

        DateTime dateTime = Utils.dobStringToDateTime(dobString);
        List<Map<String, Object>> sch = VaccinatorUtils.generateScheduleList(Constants.KEY.CHILD, dateTime, receivedVaccines, alerts);

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
        wrapper.setConvertView(convertView);
        wrapper.setNv(nv);
        updateRecordVaccination(wrapper);

    }

    private void updateRecordVaccination(VaccineViewRecordUpdateWrapper updateWrapper) {
        View recordVaccination = updateWrapper.getConvertView().findViewById(R.id.record_vaccination);
        recordVaccination.setVisibility(View.VISIBLE);

        TextView recordVaccinationText = updateWrapper.getConvertView().findViewById(R.id.record_vaccination_text);
        ImageView recordVaccinationCheck = updateWrapper.getConvertView().findViewById(R.id.record_vaccination_check);
        recordVaccinationCheck.setVisibility(View.GONE);


        State state = State.WAITING;
        String stateKey = "";

        Map<String, Object> nv = updateWrapper.getNv();

        DateTime dueDate = null;
        if (nv != null) {
            if (nv.get(Constants.KEY.VACCINE) != null && nv.get(Constants.KEY.VACCINE) instanceof VaccineRepo.Vaccine) {
                VaccineRepo.Vaccine vaccine = (VaccineRepo.Vaccine) nv.get(Constants.KEY.VACCINE);
                stateKey = VaccinateActionUtils.stateKey(vaccine);
            }

            Alert alert = null;
            if (nv.get(Constants.KEY.ALERT) != null && nv.get(Constants.KEY.ALERT) instanceof Alert) {
                alert = (Alert) nv.get(Constants.KEY.ALERT);
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

                if (nv.get(Constants.KEY.DATE) != null && nv.get(Constants.KEY.DATE) instanceof DateTime) {
                    dueDate = (DateTime) nv.get(Constants.KEY.DATE);
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

        TextView nextDate = convertView.findViewById(R.id.child_next_appointment);

        if (nextDate != null && dueDate != null) {

            nextDate.setText(Utils.convertDateFormat(dueDate));
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
                        Toast.makeText(context, context.getString(R.string.show_vaccine_card_disabled), Toast.LENGTH_SHORT).show();
                    }
                });

                TextView moveToCatchmentText = catchmentView.findViewById(R.id.move_to_catchment_text);
                moveToCatchmentText.setText("Move to my\ncatchment");

                String motherBaseEntityId = getValue(pc.getColumnmaps(), "mother_" + DBConstants.KEY.BASE_ENTITY_ID, false);
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