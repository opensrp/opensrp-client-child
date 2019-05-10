package org.smartregister.child.sample.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import org.apache.commons.lang3.tuple.Triple;
import org.smartregister.child.ChildLibrary;
import org.smartregister.child.activity.BaseChildImmunizationActivity;
import org.smartregister.child.domain.RegisterClickables;
import org.smartregister.child.sample.R;
import org.smartregister.child.sample.application.SampleApplication;
import org.smartregister.child.sample.util.Utils;
import org.smartregister.child.toolbar.LocationSwitcherToolbar;
import org.smartregister.child.util.Constants;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.location.helper.LocationHelper;


import java.util.Locale;

public class ChildImmunizationActivity extends BaseChildImmunizationActivity {
    private Spinner languageSpinner;
    int check = 0;
    private static String[] langArray;

    private static final String TAG = "ChildImmunization";

    @Override
    protected void attachBaseContext(android.content.Context base) {
        String lang = Utils.getLanguage(base.getApplicationContext());
        super.attachBaseContext(Utils.setAppLocale(base, lang));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        attachLanguageSpinner();
    }

    @Override
    protected int getDrawerLayoutId() {
        return 0;
    }

    @Override
    protected int getToolbarId() {
        return LocationSwitcherToolbar.TOOLBAR_ID;
    }

    @Override
    public boolean isLastModified() {
        SampleApplication application = (SampleApplication) getApplication();
        return application.isLastModified();
    }

    @Override
    public void setLastModified(boolean lastModified) {
        SampleApplication application = (SampleApplication) getApplication();
        if (lastModified != application.isLastModified()) {
            application.setLastModified(lastModified);
        }
    }

    @Override
    public void onUniqueIdFetched(Triple<String, String, String> triple, String entityId) {

    }

    @Override
    public void onNoUniqueId() {

    }

    @Override
    protected Activity getActivity() {
        return this;

    }

    @Override
    protected void goToRegisterPage() {

        Intent intent = new Intent(this, ChildRegisterActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    @Override
    public void onRegistrationSaved(boolean isEdit) {

    }

    public void launchDetailActivity(Context fromContext, CommonPersonObjectClient childDetails, RegisterClickables registerClickables) {

        Intent intent = new Intent(fromContext, ChildDetailTabbedActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString(Constants.KEY.LOCATION_NAME, LocationHelper.getInstance().getOpenMrsLocationId(getCurrentLocation()));
        bundle.putSerializable(Constants.INTENT_KEY.EXTRA_CHILD_DETAILS, childDetails);
        bundle.putSerializable(Constants.INTENT_KEY.EXTRA_REGISTER_CLICKABLES, registerClickables);
        intent.putExtras(bundle);

        fromContext.startActivity(intent);
    }


    private void attachLanguageSpinner() {
        languageSpinner = findViewById(R.id.language_spinner);
        langArray = new String[]{"English", "Français", "عربى"};

        final ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getApplicationContext(),
                R.array.languages, R.layout.language_spinner_item);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        languageSpinner.setAdapter(adapter);

        languageSpinner.setOnItemSelectedListener(null);
        String langPref = Utils.getLanguage(getApplicationContext());
        for (int i = 0; i < langArray.length; i++) {

            if (langPref != null && langArray[i].toLowerCase().startsWith(langPref)) {
                languageSpinner.setSelection(i);
                break;
            }
        }

        languageSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (check++ == 0) return;

                Log.d("ChildImmunization", "Selected " + adapter.getItem(i));

                String lang = adapter.getItem(i).toString().toLowerCase();
                Locale LOCALE;
                switch (lang) {
                    case "english":
                        LOCALE = Locale.ENGLISH;
                        break;
                    case "français":
                        LOCALE = Locale.FRENCH;
                        break;
                    case "عربى":
                        LOCALE = new Locale("ar");
                        break;
                    default:
                        LOCALE = Locale.ENGLISH;
                        break;
                }

                Utils.saveLanguage(getApplicationContext(), LOCALE.getLanguage());
                Log.d("LANGUAGE", ChildLibrary.getInstance().context().allSharedPreferences().fetchLanguagePreference());

                // update context as well
                Locale locale = new Locale(LOCALE.getLanguage());
                Resources res = getApplicationContext().getResources();
                DisplayMetrics dm = res.getDisplayMetrics();
                Configuration conf = res.getConfiguration();
                conf.locale = locale;
                res.updateConfiguration(conf, dm);
                ChildLibrary.getInstance().context().updateApplicationContext(getApplicationContext());

                Intent inte = new Intent(ChildImmunizationActivity.this, ChildRegisterActivity.class);
                startActivity(inte.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }
}
