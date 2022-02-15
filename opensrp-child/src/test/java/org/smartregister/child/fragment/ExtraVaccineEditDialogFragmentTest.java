package org.smartregister.child.fragment;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.smartregister.child.R;
import org.smartregister.child.TestChildApp;
import org.smartregister.child.util.Constants;
import org.smartregister.commonregistry.CommonPersonObjectClient;

import java.util.HashMap;
import java.util.Map;

@RunWith(RobolectricTestRunner.class)
@Config(application = TestChildApp.class, sdk = 27)
public class ExtraVaccineEditDialogFragmentTest {

    private AppCompatActivity activity;
    private ExtraVaccineEditDialogFragment extraVaccineFragment;
    private final String baseEntityId = "e67fecfd-20f6-46d3-834e-fc09e575676c";
    private final String serviceDate = "2020-02-02";
    private final String vaccineName = "BCG Booster";
    private final String firstName = "Johnson";
    private final String lastName = "Jambo";
    private final String fullName = "Johnson Jambo";
    private final String zeirId = "12345678";
    private final String dob = "2021-03-24T08:00:00.000-04:00";

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    @Before
    public void setUp() {
        activity = Robolectric.buildActivity(AppCompatActivity.class).create().resume().get();
        extraVaccineFragment = ExtraVaccineEditDialogFragment.newInstance();
    }

    @After
    public void tearDown(){
        try {
            activity.finish();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    @Ignore("TODO: Resolve out of memory error")
    public void testThatViewsAreInitializedCorrectly() {

        initFragment();

        View view = extraVaccineFragment.getView();
        TextView nameView = view.findViewById(R.id.name);
        Assert.assertEquals(nameView.getText().toString(), fullName);

        TextView zierIdView = view.findViewById(R.id.number);
        Assert.assertEquals(zierIdView.getText().toString(), zeirId);

        TextView vaccineView = view.findViewById(R.id.vaccine);
        Assert.assertEquals(vaccineView.getText().toString(), vaccineName);

        TextView serviceDateTextView = view.findViewById(R.id.service_date);
        Assert.assertEquals(serviceDateTextView.getText().toString(), String.format("%s: %s", activity.getString(R.string.service_date), serviceDate));

    }

    @Test
    @Ignore("TODO: Resolve out of memory error")
    public void testButtonClicks() {
        initFragment();

        View view = extraVaccineFragment.getView();
        Button cancelButton = view.findViewById(R.id.vaccinate_today);
        cancelButton.performClick();
        final Button setButton = view.findViewById(R.id.set);
        Assert.assertEquals(setButton.getVisibility(), View.VISIBLE);
    }

    public void initFragment() {

        Map<String, String> details = new HashMap<String, String>() {{
            put(Constants.KEY.ZEIR_ID, zeirId);
            put(Constants.KEY.FIRST_NAME, firstName);
            put(Constants.KEY.LAST_NAME, lastName);
            put(Constants.KEY.SERVICE_DATE, serviceDate);
            put(Constants.KEY.VACCINE, vaccineName);
            put(Constants.KEY.BASE_ENTITY_ID, baseEntityId);
            put(Constants.KEY.DOB, dob);
        }};

        CommonPersonObjectClient childDetails = new CommonPersonObjectClient(baseEntityId, details, fullName);
        childDetails.setColumnmaps(new HashMap<String, String>() {{
            putAll(details);
        }});

        Bundle arguments = new Bundle();
        arguments.putSerializable(Constants.KEY.DETAILS, childDetails);
        arguments.putString(Constants.KEY.BASE_ENTITY_ID, baseEntityId);
        extraVaccineFragment.setArguments(arguments);

        activity.getSupportFragmentManager()
                .beginTransaction()
                .add(extraVaccineFragment, ExtraVaccineEditDialogFragment.TAG)
                .commitNow();
    }
}