package org.smartregister.child.fragment;

import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.vijay.jsonwizard.utils.FormUtils;
import com.vijay.jsonwizard.utils.NativeFormsProperties;

import org.greenrobot.eventbus.EventBus;
import org.joda.time.DateTime;
import org.smartregister.child.R;
import org.smartregister.child.domain.ExtraVaccineUpdateEvent;
import org.smartregister.child.util.Constants;
import org.smartregister.clientandeventmodel.DateUtil;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.immunization.domain.VaccineSchedule;
import org.smartregister.immunization.util.ImageUtils;
import org.smartregister.immunization.util.Utils;
import org.smartregister.immunization.util.VaccinatorUtils;
import org.smartregister.util.OpenSRPImageLoader;
import org.smartregister.view.activity.DrishtiApplication;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import timber.log.Timber;

import static org.smartregister.child.util.Utils.getValue;

public class EditExtraVaccineFragment extends DialogFragment {

    public static final String TAG = EditExtraVaccineFragment.class.getSimpleName();

    private static EditExtraVaccineFragment editExtraVaccineFragment;

    private CommonPersonObjectClient details;

    private String baseEntityId;

    private EditExtraVaccineFragment() {
    }

    @Override
    public void onStart() {
        super.onStart();
        new Handler().post(() -> {
            Window window = requireDialog().getWindow();
            if (window != null) {
                Point point = new Point();
                window.getWindowManager().getDefaultDisplay().getSize(point);
                double widthFactor = Utils.calculateDialogWidthFactor(requireActivity());
                window.setLayout((int) (point.x * widthFactor), FrameLayout.LayoutParams.WRAP_CONTENT);
                window.setGravity(Gravity.CENTER);
            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        baseEntityId = requireArguments().getString(Constants.KEY.BASE_ENTITY_ID);
        details = (CommonPersonObjectClient) requireArguments().getSerializable(Constants.KEY.DETAILS);

        View view = inflater.inflate(R.layout.vaccination_edit_dialog_view, container, true);

        updateChildProfile(view);

        TextView nameView = view.findViewById(R.id.name);
        String childName = getValue(details, Constants.KEY.FIRST_NAME, true) + " " +
                getValue(details, Constants.KEY.LAST_NAME, true);
        nameView.setText(childName);

        TextView openSrpIdTextView = view.findViewById(R.id.number);
        String childId = getValue(details, Constants.KEY.ZEIR_ID, "", false).replace("-", "");
        openSrpIdTextView.setText(childId);

        View vaccinationName = inflater.inflate(R.layout.vaccination_name_edit_dialog, null);
        TextView vaccineView = vaccinationName.findViewById(org.smartregister.immunization.R.id.vaccine);
        String vaccine = details.getDetails().get(Constants.KEY.VACCINE);
        vaccineView.setText(VaccinatorUtils.getTranslatedVaccineName(requireContext(), vaccine));
        final LinearLayout vaccinationNameLayout = view.findViewById(org.smartregister.immunization.R.id.vaccination_name_layout);
        vaccinationNameLayout.addView(vaccinationName);

        TextView serviceDateTextView = view.findViewById(R.id.service_date);
        String serviceDate = details.getDetails().get(Constants.KEY.SERVICE_DATE);
        serviceDateTextView.setText(String.format("%s: %s", getString(R.string.service_date), serviceDate));

        initButtons(view, vaccine, serviceDate);

        return view;
    }

    private void initButtons(View dialogView, String vaccine, String serviceDate) {

        SimpleDateFormat nativeFormFormat = new SimpleDateFormat(FormUtils.NATIIVE_FORM_DATE_FORMAT_PATTERN, Locale.ENGLISH);

        dialogView.findViewById(R.id.cancel).setOnClickListener(view -> dismiss());

        boolean isNumeric = Utils.isPropertyTrue(NativeFormsProperties.KEY.WIDGET_DATEPICKER_IS_NUMERIC);
        final DatePicker vaccineDatePicker = dialogView.findViewById(isNumeric ? R.id.earlier_date_picker_numeric : R.id.earlier_date_picker);

        try {
            Date parsedServiceDate = nativeFormFormat.parse(serviceDate);
            if (parsedServiceDate != null) {
                setFormattedDates(vaccineDatePicker, parsedServiceDate);

                final Button setButton = dialogView.findViewById(R.id.set);
                setButton.setOnClickListener(view -> {
                    int day = vaccineDatePicker.getDayOfMonth();
                    int month = vaccineDatePicker.getMonth();
                    int year = vaccineDatePicker.getYear();

                    Calendar calendar = Calendar.getInstance();
                    calendar.set(Calendar.YEAR, year);
                    calendar.set(Calendar.MONTH, month);
                    calendar.set(Calendar.DAY_OF_MONTH, day);
                    DateTime dateTime = new DateTime(calendar.getTime());

                    EventBus.getDefault().post(new ExtraVaccineUpdateEvent(baseEntityId, vaccine, DateUtil.yyyyMMdd.format(dateTime.toDate())));
                    dismiss();
                });

                final Button serviceDateButton = dialogView.findViewById(R.id.vaccinate_today);
                serviceDateButton.setOnClickListener(view -> {
                    vaccineDatePicker.setVisibility(View.VISIBLE);
                    setButton.setVisibility(View.VISIBLE);
                });

                final Button undoButton = dialogView.findViewById(R.id.vaccinate_earlier);
                undoButton.setOnClickListener(view -> {
                    EventBus.getDefault().post(new ExtraVaccineUpdateEvent(baseEntityId, vaccine, DateUtil.yyyyMMdd.format(parsedServiceDate), true));
                    dismiss();
                });
            }
        } catch (ParseException e) {
            Timber.e(e, "Unable to Parse Service Date");
        }
    }

    private void setFormattedDates(DatePicker vaccineDatePicker, Date serviceDate) {
        Calendar serviceDateCalender = Calendar.getInstance();
        serviceDateCalender.setTime(serviceDate);
        vaccineDatePicker.updateDate(serviceDateCalender.get(Calendar.YEAR), serviceDateCalender.get(Calendar.MONTH), serviceDateCalender.get(Calendar.DATE));

        Calendar today = Calendar.getInstance();
        Calendar minDate = Calendar.getInstance();
        VaccineSchedule.standardiseCalendarDate(today);
        String dobString = org.smartregister.child.util.Utils.getValue(details.getColumnmaps(), Constants.KEY.DOB, false);
        Date dob = org.smartregister.child.util.Utils.dobStringToDate(dobString);
        if (dob != null) {
            minDate.setTime(dob);
            VaccineSchedule.standardiseCalendarDate(minDate);
            vaccineDatePicker.setMinDate(minDate.getTimeInMillis());
        }
        vaccineDatePicker.setMaxDate(today.getTimeInMillis());
    }

    private void updateChildProfile(View dialogView) {
        ImageView mImageView = dialogView.findViewById(R.id.child_profilepic);
        String baseEntityId = details.getCaseId();
        if (baseEntityId != null) {
            mImageView.setTag(R.id.entity_id, baseEntityId);
            String gender = details.getDetails().get(Constants.KEY.GENDER);

            int defaultImageResId = ImageUtils.profileImageResourceByGender(gender);
            int errorImageResId = ImageUtils.profileImageResourceByGender(gender);
            DrishtiApplication.getCachedImageLoaderInstance().getImageByClientId(baseEntityId,
                    OpenSRPImageLoader.getStaticImageListener(mImageView, defaultImageResId, errorImageResId));
        }
    }

    public static EditExtraVaccineFragment newInstance() {
        if (editExtraVaccineFragment == null) {
            editExtraVaccineFragment = new EditExtraVaccineFragment();
        }
        return editExtraVaccineFragment;
    }
}
