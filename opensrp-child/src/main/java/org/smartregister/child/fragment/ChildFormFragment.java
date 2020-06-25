package org.smartregister.child.fragment;

import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatCheckBox;
import android.text.InputType;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.rengwuxian.materialedittext.MaterialEditText;
import com.vijay.jsonwizard.activities.JsonFormActivity;
import com.vijay.jsonwizard.constants.JsonFormConstants;
import com.vijay.jsonwizard.customviews.MaterialSpinner;
import com.vijay.jsonwizard.domain.Form;
import com.vijay.jsonwizard.fragments.JsonWizardFormFragment;
import com.vijay.jsonwizard.utils.FormUtils;
import com.vijay.jsonwizard.utils.ValidationStatus;
import com.vijay.jsonwizard.viewstates.JsonFormFragmentViewState;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.smartregister.child.ChildLibrary;
import org.smartregister.child.R;
import org.smartregister.child.interactor.ChildFormInteractor;
import org.smartregister.child.presenter.ChildFormFragmentPresenter;
import org.smartregister.child.provider.MotherLookUpSmartClientsProvider;
import org.smartregister.child.util.Constants;
import org.smartregister.child.util.MotherLookUpUtils;
import org.smartregister.commonregistry.CommonPersonObject;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.event.Listener;
import org.smartregister.util.Utils;

import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import timber.log.Timber;

import static org.smartregister.util.Utils.getValue;

/**
 * Created by ndegwamartin on 01/03/2019.
 */
public class ChildFormFragment extends JsonWizardFormFragment {

    public static final String TAG = ChildFormFragment.class.getName();

    private Snackbar snackbar = null;
    private AlertDialog alertDialog = null;
    private boolean lookedUp = false;
    private MaterialEditText motherDOBMaterialEditText;
    private AppCompatCheckBox compatCheckBox;
    private MaterialSpinner spinner;

    private static final int showResultsDuration = Integer.valueOf(ChildLibrary
            .getInstance()
            .getProperties()
            .getProperty(Constants.PROPERTY.MOTHER_LOOKUP_SHOW_RESULTS_DURATION, Constants.MOTHER_LOOKUP_SHOW_RESULTS_DEFAULT_DURATION));

    private static final int undoChoiceDuration = Integer.valueOf(ChildLibrary
            .getInstance()
            .getProperties()
            .getProperty(Constants.PROPERTY.MOTHER_LOOKUP_UNDO_DURATION, Constants.MOTHER_LOOKUP_UNDO_DEFAULT_DURATION));

    private final View.OnClickListener lookUpRecordOnClickLister = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (alertDialog != null && alertDialog.isShowing()) {
                alertDialog.dismiss();
                CommonPersonObjectClient client = null;
                if (view.getTag() != null && view.getTag() instanceof CommonPersonObjectClient) {
                    client = (CommonPersonObjectClient) view.getTag();
                }

                if (client != null) {
                    lookupDialogDismissed(client);
                }
            }
        }
    };
    private final Listener<HashMap<CommonPersonObject, List<CommonPersonObject>>> motherLookUpListener =
            new Listener<HashMap<CommonPersonObject, List<CommonPersonObject>>>() {
                @Override
                public void onEvent(HashMap<CommonPersonObject, List<CommonPersonObject>> data) {
                    if (!lookedUp) {
                        showMotherLookUp(data);
                    }
                }
            };

    public static ChildFormFragment getFormFragment(String stepName) {
        ChildFormFragment jsonFormFragment = new ChildFormFragment();
        Bundle bundle = new Bundle();
        bundle.putString(JsonFormConstants.JSON_FORM_KEY.STEPNAME, stepName);
        jsonFormFragment.setArguments(bundle);
        return jsonFormFragment;
    }

    @Override
    protected JsonFormFragmentViewState createViewState() {
        return new JsonFormFragmentViewState();
    }

    @Override
    protected ChildFormFragmentPresenter createPresenter() {
        return new ChildFormFragmentPresenter(this, ChildFormInteractor.getChildInteractorInstance());
    }

    @Override
    public void updateVisibilityOfNextAndSave(boolean next, boolean save) {
        super.updateVisibilityOfNextAndSave(next, save);
        Form form = getForm();
        if (form != null && form.isWizard() &&
                !ChildLibrary.getInstance().metadata().formWizardValidateRequiredFieldsBefore) {
            this.getMenu().findItem(com.vijay.jsonwizard.R.id.action_save).setVisible(save);
        }
    }

    private Form getForm() {
        return this.getActivity() != null && this.getActivity() instanceof JsonFormActivity ?
                ((JsonFormActivity) this.getActivity()).getForm() : null;
    }

    public void validateActivateNext() {
        if (!isVisible()) { //form fragment is initializing or not the last page
            return;
        }

        Form form = getForm();
        if (form == null || !form.isWizard()) {
            return;
        }

        ValidationStatus validationStatus = null;
        for (View dataView : getJsonApi().getFormDataViews()) {
            validationStatus = getPresenter().validate(this, dataView, false);
            if (!validationStatus.isValid()) {
                break;
            }
        }

        if (validationStatus != null && validationStatus.isValid()) {
            if (!getPresenter().intermediatePage()) {
                getMenu().findItem(com.vijay.jsonwizard.R.id.action_save).setVisible(true);
            }
        } else {
            if (!getPresenter().intermediatePage()) {
                getMenu().findItem(com.vijay.jsonwizard.R.id.action_save).setVisible(false);
            }
        }
    }

    public ChildFormFragmentPresenter getPresenter() {
        return (ChildFormFragmentPresenter) presenter;
    }

    //Mother Lookup
    public Listener<HashMap<CommonPersonObject, List<CommonPersonObject>>> motherLookUpListener() {
        return motherLookUpListener;
    }

    private void showMotherLookUp(final HashMap<CommonPersonObject, List<CommonPersonObject>> map) {
        if (!map.isEmpty()) {
            tapToView(map);
        } else {
            if (snackbar != null) {
                snackbar.dismiss();
            }
        }
    }

    private void tapToView(final HashMap<CommonPersonObject, List<CommonPersonObject>> map) {
        snackbar = Snackbar.make(getMainView(), getActivity().getString(R.string.mother_guardian_matches, String.valueOf(map.size())),
                Snackbar.LENGTH_INDEFINITE);
        snackbar.setAction(R.string.tap_to_view, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateResults(map);
                //updateResultTree(map);
            }
        });
        show(snackbar);

    }

    private void updateResults(final HashMap<CommonPersonObject, List<CommonPersonObject>> map) {
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.mother_lookup_results, null);

        ListView listView = view.findViewById(R.id.list_view);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.PathDialog);
        builder.setView(view).setNegativeButton(R.string.dismiss, null);
        builder.setCancelable(true);

        alertDialog = builder.create();

        final List<CommonPersonObject> mothers = new ArrayList<>();
        for (Map.Entry<CommonPersonObject, List<CommonPersonObject>> entry : map.entrySet()) {
            mothers.add(entry.getKey());
        }

        final MotherLookUpSmartClientsProvider motherLookUpSmartClientsProvider =
                new MotherLookUpSmartClientsProvider(getActivity());
        BaseAdapter baseAdapter = new BaseAdapter() {
            @Override
            public int getCount() {
                return mothers.size();
            }

            @Override
            public Object getItem(int position) {
                return mothers.get(position);
            }

            @Override
            public long getItemId(int position) {
                return new BigInteger(mothers.get(position).getCaseId().replaceAll("\\D+", "")).longValue();
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View v;
                if (convertView == null) {
                    v = motherLookUpSmartClientsProvider.inflateLayoutForCursorAdapter();
                } else {
                    v = convertView;
                }

                CommonPersonObject commonPersonObject = mothers.get(position);
                List<CommonPersonObject> children = map.get(commonPersonObject);

                motherLookUpSmartClientsProvider.getView(commonPersonObject, children, v);

                v.setOnClickListener(lookUpRecordOnClickLister);
                v.setTag(Utils.convert(commonPersonObject));

                return v;
            }
        };

        listView.setAdapter(baseAdapter);
        alertDialog.show();

    }

    private void show(final Snackbar snackbar) {
        if (snackbar == null) {
            return;
        }

        float drawablePadding = getResources().getDimension(R.dimen.register_drawable_padding);
        int paddingInt = Float.valueOf(drawablePadding).intValue();

        float textSize = getActivity().getResources().getDimension(R.dimen.snack_bar_text_size);

        View snackbarView = snackbar.getView();
        snackbarView.setMinimumHeight(Float.valueOf(textSize).intValue());
        snackbarView.setBackgroundResource(R.color.accent);

        final Button actionView = snackbarView.findViewById(android.support.design.R.id.snackbar_action);
        actionView.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
        actionView.setGravity(Gravity.CENTER);
        actionView.setTextColor(getResources().getColor(R.color.white));

        TextView textView = snackbarView.findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
        textView.setGravity(Gravity.CENTER);
        textView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_error, 0, 0, 0);
        textView.setCompoundDrawablePadding(paddingInt);
        textView.setPadding(paddingInt, 0, 0, 0);
        textView.setTextColor(getResources().getColor(R.color.white));

        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                actionView.performClick();
            }
        });

        snackbarView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                actionView.performClick();
            }
        });

        snackbar.show();

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                snackbar.dismiss();
            }
        }, showResultsDuration);

    }

    private void showFinalActionSnackBar(final Snackbar snackbar) {
        if (snackbar == null) {
            return;
        }

        float drawablePadding = getResources().getDimension(R.dimen.register_drawable_padding);
        int paddingInt = Float.valueOf(drawablePadding).intValue();

        float textSize = getActivity().getResources().getDimension(R.dimen.snack_bar_text_size);

        View snackbarView = snackbar.getView();
        snackbarView.setMinimumHeight(Float.valueOf(textSize).intValue());
        snackbarView.setBackgroundResource(R.color.accent);

        final Button actionView = snackbarView.findViewById(android.support.design.R.id.snackbar_action);
        actionView.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
        actionView.setGravity(Gravity.CENTER);
        actionView.setTextColor(getResources().getColor(R.color.white));

        TextView textView = snackbarView.findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
        textView.setGravity(Gravity.CENTER);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                clearMotherLookUp();
            }
        });
        textView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_error, 0, 0, 0);
        textView.setCompoundDrawablePadding(paddingInt);
        textView.setPadding(paddingInt, 0, 0, 0);
        textView.setTextColor(getResources().getColor(R.color.white));
        textView.setAllCaps(true);
        textView.setTypeface(Typeface.DEFAULT_BOLD);

        snackbarView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // actionView.performClick();
            }
        });

        snackbar.show();

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                snackbar.dismiss();
            }
        }, showResultsDuration);

    }

    private void lookupDialogDismissed(CommonPersonObjectClient pc) {
        if (pc != null) {

            Locale locale = getActivity().getResources().getConfiguration().locale;
            SimpleDateFormat mlsLookupDateFormatter = new SimpleDateFormat(FormUtils.NATIIVE_FORM_DATE_FORMAT_PATTERN, locale.getLanguage().equals("ar") ? Locale.ENGLISH : locale);

            Map<String, List<View>> lookupMap = getLookUpMap();
            if (lookupMap.containsKey(Constants.KEY.MOTHER)) {
                List<View> lookUpViews = lookupMap.get(Constants.KEY.MOTHER);
                if (lookUpViews != null && !lookUpViews.isEmpty()) {

                    for (View view : lookUpViews) {

                        String key = (String) view.getTag(com.vijay.jsonwizard.R.id.key);
                        String text = "";

                        if (StringUtils.containsIgnoreCase(key, MotherLookUpUtils.firstName)) {
                            text = getValue(pc.getColumnmaps(), MotherLookUpUtils.firstName, true);
                        }

                        if (StringUtils.containsIgnoreCase(key, MotherLookUpUtils.lastName)) {
                            text = getValue(pc.getColumnmaps(), MotherLookUpUtils.lastName, true);
                        }

                        if (StringUtils.endsWithIgnoreCase(key, MotherLookUpUtils.birthDate)) {
                            String dobString = getValue(pc.getColumnmaps(), MotherLookUpUtils.dob, false);
                            Date motherDob = Utils.dobStringToDate(dobString);
                            if (motherDob != null) {
                                try {
                                    text = mlsLookupDateFormatter.format(motherDob);
                                } catch (Exception e) {
                                    Timber.e(e);
                                }
                            }

                            motherDOBMaterialEditText = (MaterialEditText) view;
                        }

                        if (StringUtils.containsIgnoreCase(key, MotherLookUpUtils.MOTHER_GUARDIAN_NRC)) {
                            text = getValue(pc.getColumnmaps(), MotherLookUpUtils.NRC_NUMBER, true);
                        }

                        if (StringUtils.containsIgnoreCase(key, MotherLookUpUtils.MOTHER_GUARDIAN_PHONE_NUMBER)) {
                            text = getValue(pc.getColumnmaps(), MotherLookUpUtils.MOTHER_GUARDIAN_PHONE_NUMBER.toLowerCase(Locale.ENGLISH), true);
                        }

                        if (key.equalsIgnoreCase(Constants.RESIDENTIAL_AREA)) {
                            text = getValue(pc.getColumnmaps(), Constants.RESIDENTIAL_AREA, true);
                        }


                        if (key.equalsIgnoreCase(Constants.RESIDENTIAL_AREA_OTHER)) {
                            text = getValue(pc.getColumnmaps(), Constants.RESIDENTIAL_AREA_OTHER.toLowerCase(Locale.ENGLISH), true);
                        }

                        if (key.equalsIgnoreCase(Constants.RESIDENTIAL_ADDRESS)) {
                            text = getValue(pc.getColumnmaps(), Constants.RESIDENTIAL_ADDRESS.toLowerCase(Locale.ENGLISH), true);
                        }


                        if (key.equalsIgnoreCase(Constants.PREFERRED_LANGUAGE)) {
                            text = getValue(pc.getColumnmaps(), Constants.PREFERRED_LANGUAGE.toLowerCase(Locale.ENGLISH), true);
                        }

                        if (StringUtils.equalsIgnoreCase(key, MotherLookUpUtils.IS_CONSENTED)) {
                            text = getValue(pc.getColumnmaps(), MotherLookUpUtils.IS_CONSENTED, false);
                        }


                        if (view instanceof MaterialEditText) {
                            MaterialEditText materialEditText = (MaterialEditText) view;
                            materialEditText.setEnabled(false);
                            materialEditText.setTag(com.vijay.jsonwizard.R.id.after_look_up, true);
                            materialEditText.setText(text);
                            materialEditText.setInputType(InputType.TYPE_NULL);

                        } else {

                            if ("Mother_Guardian_Date_Birth_Unknown".equalsIgnoreCase(key)) {

                                view.setVisibility(View.GONE);

                            } else if (Constants.PREFERRED_LANGUAGE.equalsIgnoreCase(key)) {

                                spinner = ((MaterialSpinner) ((ViewGroup) (view)).getChildAt(0));

                                try {
                                    JSONArray itemKeys = (JSONArray) spinner.getTag(com.vijay.jsonwizard.R.id.keys);

                                    int selected = 0;
                                    for (int i = 0; i < itemKeys.length(); i++) {

                                        if (itemKeys.get(i).toString().equalsIgnoreCase(text)) {
                                            selected = i;
                                            break;
                                        }
                                    }

                                    spinner.setSelection(selected);

                                } catch (JSONException e) {

                                    Timber.e(e, e.getMessage());
                                }

                                spinner.setEnabled(false);

                            } else if (MotherLookUpUtils.IS_CONSENTED.equalsIgnoreCase(key)) {

                                compatCheckBox = (AppCompatCheckBox) ((ViewGroup) ((LinearLayout) view).getChildAt(1)).getChildAt(0);
                                compatCheckBox.setChecked(StringUtils.containsIgnoreCase(text, MotherLookUpUtils.IS_CONSENTED));
                                compatCheckBox.setEnabled(false);

                            }
                        }

                    }

                    Map<String, String> metadataMap = new HashMap<>();
                    metadataMap.put(Constants.KEY.ENTITY_ID, Constants.KEY.MOTHER);
                    metadataMap.put(Constants.KEY.VALUE, getValue(pc.getColumnmaps(), MotherLookUpUtils.baseEntityId, false));

                    writeMetaDataValue(FormUtils.LOOK_UP_JAVAROSA_PROPERTY, metadataMap);

                    lookedUp = true;
                    clearView();

                    //Fix weird bug, Mother DOB not disabled on auto-look up
                    if (motherDOBMaterialEditText != null) {
                        motherDOBMaterialEditText.setEnabled(false);
                    }
                }
            }
        }
    }

    private void clearView() {
        snackbar = Snackbar.make(getMainView(), R.string.undo_lookup, Snackbar.LENGTH_INDEFINITE);
        snackbar.setDuration(undoChoiceDuration);
        snackbar.setAction(R.string.dismiss_lookup, new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                snackbar.dismiss();

            }
        });
        showFinalActionSnackBar(snackbar);
    }

    private void clearMotherLookUp() {
        Map<String, List<View>> lookupMap = getLookUpMap();
        if (lookupMap.containsKey(Constants.KEY.MOTHER)) {
            List<View> lookUpViews = lookupMap.get(Constants.KEY.MOTHER);
            if (lookUpViews != null && !lookUpViews.isEmpty()) {
                for (View view : lookUpViews) {
                    if (view instanceof MaterialEditText) {
                        MaterialEditText materialEditText = (MaterialEditText) view;
                        materialEditText.setEnabled(true);
                        enableEditText(materialEditText);
                        materialEditText.setTag(com.vijay.jsonwizard.R.id.after_look_up, false);
                        materialEditText.setText("");
                    }
                }

                Map<String, String> metadataMap = new HashMap<>();
                metadataMap.put(Constants.KEY.ENTITY_ID, "");
                metadataMap.put(Constants.KEY.VALUE, "");

                writeMetaDataValue(FormUtils.LOOK_UP_JAVAROSA_PROPERTY, metadataMap);

                lookedUp = false;

                //Fix weird bug, Mother DOB not disabled on auto-look up
                if (motherDOBMaterialEditText != null) {
                    motherDOBMaterialEditText.setEnabled(true);
                    enableEditText(motherDOBMaterialEditText);
                    motherDOBMaterialEditText.setTag(com.vijay.jsonwizard.R.id.after_look_up, false);
                    motherDOBMaterialEditText.setText("");
                }

                // Clean up field widgets
                if (spinner != null) {
                    spinner.setEnabled(true);
                    spinner.setSelection(0);
                }

                if (compatCheckBox != null) {
                    compatCheckBox.setEnabled(true);
                    compatCheckBox.setChecked(false);
                }

            }
        }
    }

    private void enableEditText(MaterialEditText editText) {
        editText.setInputType(InputType.TYPE_CLASS_TEXT);
    }

    public void getLabelViewFromTag(String labeltext, String todisplay) {
        updateRelevantTextView(getMainView(), todisplay, labeltext);
    }

    private void updateRelevantTextView(LinearLayout mMainView, String textstring, String currentKey) {
        if (mMainView != null) {
            int childCount = mMainView.getChildCount();
            for (int i = 0; i < childCount; i++) {
                View view = mMainView.getChildAt(i);
                if (view instanceof TextView) {
                    TextView textView = (TextView) view;
                    String key = (String) textView.getTag(com.vijay.jsonwizard.R.id.key);
                    if (key.equals(currentKey)) {
                        textView.setText(textstring);
                    }
                }
            }
        }
    }

    public String getRelevantTextViewString(String currentKey) {
        String toreturn = "";
        if (getMainView() != null) {
            int childCount = getMainView().getChildCount();
            for (int i = 0; i < childCount; i++) {
                View view = getMainView().getChildAt(i);
                if (view instanceof TextView) {
                    TextView textView = (TextView) view;
                    String key = (String) textView.getTag(com.vijay.jsonwizard.R.id.key);
                    if (key.equals(currentKey)) {
                        toreturn = textView.getText().toString();
                    }
                }
            }
        }
        return toreturn;
    }


}
