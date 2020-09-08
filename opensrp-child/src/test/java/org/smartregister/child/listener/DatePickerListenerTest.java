package org.smartregister.child.listener;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.widget.EditText;

import com.vijay.jsonwizard.customviews.DatePickerDialog;
import com.vijay.jsonwizard.utils.FormUtils;
import com.vijay.jsonwizard.utils.NativeFormsProperties;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.robolectric.Robolectric;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.util.ReflectionHelpers;
import org.smartregister.child.BaseUnitTest;
import org.smartregister.child.ChildLibrary;
import org.smartregister.child.activity.BaseChildFormActivity;
import org.smartregister.util.AppProperties;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DatePickerListenerTest extends BaseUnitTest {

    private DatePickerListener datePickerListener;

    @Mock
    private EditText editText;

    @Mock
    private ChildLibrary childLibrary;

    @Mock
    private AppProperties appProperties;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        Activity childFormActivity = Robolectric.buildActivity(BaseChildFormActivity.class).get();
        datePickerListener = new DatePickerListener(
                childFormActivity,
                editText,
                true);
    }


    @Test
    public void testOnClickShouldCreateDatePickerDialogCorrectly() throws ParseException {
        Mockito.doReturn(true).when(appProperties).getPropertyBoolean(NativeFormsProperties.KEY.WIDGET_DATEPICKER_IS_NUMERIC);
        Mockito.doReturn(true).when(appProperties).hasProperty(NativeFormsProperties.KEY.WIDGET_DATEPICKER_IS_NUMERIC);
        Mockito.doReturn(appProperties).when(childLibrary).getProperties();
        ReflectionHelpers.setStaticField(ChildLibrary.class, "instance", childLibrary);
        String dateString = "2020-09-09";
        EditText editText = new EditText(RuntimeEnvironment.application);
        editText.setText(dateString);
        DatePickerListener datePickerListenerSpy = Mockito.spy(datePickerListener);
        DatePickerDialog datePickerDialog = Mockito.spy(new DatePickerDialog());
        Mockito.doReturn(datePickerDialog).when(datePickerListenerSpy).getDatePickerDialog();
        datePickerListenerSpy.onClick(editText);

        Mockito.verify(datePickerDialog, Mockito.times(1))
                .setNumericDatePicker(Mockito.eq(true));

        Mockito.verify(datePickerDialog, Mockito.times(1))
                .setMaxDate(Mockito.anyLong());

        Mockito.verify(datePickerDialog, Mockito.times(1))
                .setCalendarViewShown(Mockito.eq(false));

        Date date = new SimpleDateFormat(FormUtils.NATIIVE_FORM_DATE_FORMAT_PATTERN, Locale.getDefault().toString().startsWith("ar") ? Locale.ENGLISH : Locale.getDefault()).parse(dateString);
        Mockito.verify(datePickerDialog, Mockito.times(1))
                .setDate(date);

        Mockito.verify(datePickerDialog, Mockito.times(1))
                .show(Mockito.any(FragmentTransaction.class), Mockito.eq(DatePickerListener.class.getCanonicalName()));
    }


    @After
    public void tearDown() {
        ReflectionHelpers.setStaticField(ChildLibrary.class, "instance", null);
    }
}