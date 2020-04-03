package org.smartregister.child.listener;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;

import com.vijay.jsonwizard.customviews.DatePickerDialog;
import com.vijay.jsonwizard.utils.NativeFormsProperties;

import org.smartregister.child.ChildLibrary;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DatePickerListener implements View.OnClickListener {
    public static final String TAG = DatePickerListener.class.getCanonicalName();

    private final EditText editText;
    private boolean maxDateToday = false;
    private Context context;

    public DatePickerListener(Context context, EditText editText, boolean maxDateToday) {
        this.context = context;
        this.editText = editText;
        this.maxDateToday = maxDateToday;
    }

    @Override
    public void onClick(View view) {

        final SimpleDateFormat dateFormatter = new SimpleDateFormat(com.vijay.jsonwizard.utils.FormUtils.NATIIVE_FORM_DATE_FORMAT_PATTERN, Locale.getDefault().toString().startsWith("ar") ? Locale.ENGLISH : Locale.getDefault());

        //To show current date in the datepicker
        Calendar mcurrentDate = Calendar.getInstance();

        String previouslySelectedDateString = "";

        if (view instanceof EditText) {
            previouslySelectedDateString = ((EditText) view).getText().toString();

            if (!("").equals(previouslySelectedDateString) && previouslySelectedDateString.length() > 2) {
                try {
                    Date previouslySelectedDate = dateFormatter.parse(previouslySelectedDateString);
                    mcurrentDate.setTime(previouslySelectedDate);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }

        DatePickerDialog mDatePicker = new DatePickerDialog();
        boolean isNumericDatePicker = ChildLibrary.getInstance().getProperties().hasProperty(NativeFormsProperties.KEY.WIDGET_DATEPICKER_IS_NUMERIC) && ChildLibrary.getInstance().getProperties().getPropertyBoolean(NativeFormsProperties.KEY.WIDGET_DATEPICKER_IS_NUMERIC);
        mDatePicker.setNumericDatePicker(isNumericDatePicker);
        mDatePicker.setContext(context);
        mDatePicker.setOnDateSetListener(new android.app.DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datepicker, int selectedyear, int selectedmonth, int selectedday) {
                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.YEAR, selectedyear);
                calendar.set(Calendar.MONTH, selectedmonth);
                calendar.set(Calendar.DAY_OF_MONTH, selectedday);

                String dateString = dateFormatter.format(calendar.getTime());
                editText.setText(dateString);

            }
        });

        mDatePicker.setCalendarViewShown(false);

        if (maxDateToday) {
            mDatePicker.setMaxDate(new Date().getTime());
        }

        mDatePicker.setYmdOrder(new char[]{'d', 'm', 'y'});
        mDatePicker.setDate(mcurrentDate.getTime());

        //To Do : Could be refactored to move to Native forms lib
        //Call mDatePicker.show()
        //Show Dialog

        FragmentTransaction ft = ((Activity) context).getFragmentManager().beginTransaction();
        Fragment prev = ((Activity) context).getFragmentManager().findFragmentByTag(TAG);
        if (prev != null) {
            ft.remove(prev);
        }

        ft.addToBackStack(null);

        mDatePicker.show(ft, TAG);


    }

}