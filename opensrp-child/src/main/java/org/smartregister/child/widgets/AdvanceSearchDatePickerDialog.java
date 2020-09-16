package org.smartregister.child.widgets;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.widget.DatePicker;
import android.widget.EditText;

import com.vijay.jsonwizard.customviews.DatePickerDialog;
import com.vijay.jsonwizard.utils.NativeFormsProperties;

import org.smartregister.child.ChildLibrary;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class AdvanceSearchDatePickerDialog {

    private static final String TAG = AdvanceSearchDatePickerDialog.class.getSimpleName();
    private DatePickerDialog datePickerDialog;
    private EditText editText;
    private SimpleDateFormat dateFormatter;
    private Context context;

    public AdvanceSearchDatePickerDialog(EditText editText) {
        this.editText = editText;
        this.context = editText.getContext();
        initDatePickerDialog();
    }

    private void initDatePickerDialog() {
        boolean isNumericDatePicker = ChildLibrary.getInstance().getProperties().hasProperty(NativeFormsProperties.KEY.WIDGET_DATEPICKER_IS_NUMERIC)
                && ChildLibrary.getInstance().getProperties().getPropertyBoolean(NativeFormsProperties.KEY.WIDGET_DATEPICKER_IS_NUMERIC);
        datePickerDialog = new DatePickerDialog();
        datePickerDialog.setNumericDatePicker(isNumericDatePicker);
        datePickerDialog.setContext(context);
        datePickerDialog.setOnDateSetListener(new android.app.DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datepicker, int selectedYear, int selectedMonth, int selectedDay) {
                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.YEAR, selectedYear);
                calendar.set(Calendar.MONTH, selectedMonth);
                calendar.set(Calendar.DAY_OF_MONTH, selectedDay);
                editText.setText(dateFormatter.format(calendar.getTime()));
            }
        });

        datePickerDialog.setCalendarViewShown(false);
        datePickerDialog.setMaxDate(new Date().getTime());
        datePickerDialog.setYmdOrder(new char[]{'d', 'm', 'y'});
    }

    public DatePickerDialog getDatePickerDialog() {
        return datePickerDialog;
    }


    public void setDateFormatter(SimpleDateFormat dateFormatter) {
        this.dateFormatter = dateFormatter;
    }
    public void setCurrentDate(Calendar currentDate) {
        datePickerDialog.setDate(currentDate.getTime());
    }

    public void showDialog() {
        FragmentTransaction ft = ((Activity) context).getFragmentManager().beginTransaction();
        Fragment prev = ((Activity) context).getFragmentManager().findFragmentByTag(TAG);
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);
        datePickerDialog.show(ft, TAG);
    }
}
