package org.smartregister.child.sample.activity;

import android.app.FragmentTransaction;
import android.content.Intent;
import android.view.MenuItem;
import android.view.View;

import org.apache.commons.lang3.tuple.Triple;
import org.smartregister.AllConstants;
import org.smartregister.child.activity.BaseChildDetailTabbedActivity;
import org.smartregister.child.fragment.StatusEditDialogFragment;
import org.smartregister.child.sample.R;
import org.smartregister.child.sample.fragment.ChildRegistrationDataFragment;
import org.smartregister.child.task.LoadAsyncTask;
import org.smartregister.child.util.JsonFormUtils;
import org.smartregister.util.Utils;

import java.util.Arrays;
import java.util.List;

/**
 * Created by ndegwamartin on 06/03/2019.
 */
public class ChildDetailTabbedActivity extends BaseChildDetailTabbedActivity {
    private static List<String> nonEditableFields = Arrays.asList( "Sex", "ZEIR_ID", "Birth_Facility_Name", "Birth_Facility_Name_Other");

    @Override
    public void onUniqueIdFetched(Triple<String, String, String> triple, String entityId) {

    }

    @Override
    public void onNoUniqueId() {

    }

    @Override
    public ChildRegistrationDataFragment getChildRegistrationDataFragment() {
        return new ChildRegistrationDataFragment();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        switch (item.getItemId()) {
            case R.id.registration_data:

                String formJsonString = JsonFormUtils.getMetadataForEditForm(this, detailsMap, nonEditableFields);
                startFormActivity(formJsonString);
                // User chose the "Settings" item, show the app settings UI...
                return true;
            case R.id.immunization_data:
                if (viewPager.getCurrentItem() != 1) {
                    viewPager.setCurrentItem(1);
                }
                Utils.startAsyncTask(
                        new LoadAsyncTask(org.smartregister.child.enums.Status.EDIT_VACCINE, detailsMap, getChildDetails(), this, getChildDataFragment(), getChildUnderFiveFragment(), getOverflow()),
                        null);
                saveButton.setVisibility(View.VISIBLE);
                for (int i = 0; i < overflow.size(); i++) {
                    overflow.getItem(i).setVisible(false);
                }
                return true;

            case R.id.recurring_services_data:
                if (viewPager.getCurrentItem() != 1) {
                    viewPager.setCurrentItem(1);
                }
                Utils.startAsyncTask(
                        new LoadAsyncTask(org.smartregister.child.enums.Status.EDIT_SERVICE, detailsMap, getChildDetails(), this, getChildDataFragment(), getChildUnderFiveFragment(), getOverflow()),
                        null);
                saveButton.setVisibility(View.VISIBLE);
                for (int i = 0; i < overflow.size(); i++) {
                    overflow.getItem(i).setVisible(false);
                }
                return true;
            case R.id.weight_data:
                if (viewPager.getCurrentItem() != 1) {
                    viewPager.setCurrentItem(1);
                }
                Utils.startAsyncTask(
                        new LoadAsyncTask(org.smartregister.child.enums.Status.EDIT_GROWTH, detailsMap, getChildDetails(), this, getChildDataFragment(), getChildUnderFiveFragment(), getOverflow()),
                        null);


               /*  Disable hide menu items feature for Weight

                saveButton.setVisibility(View.VISIBLE);
                for (int i = 0; i < overflow.size(); i++) {
                    overflow.getItem(i).setVisible(false);
                }*/

                return true;

            case R.id.report_deceased:
                String reportDeceasedJsonFormString = getReportDeceasedMetadata();
                startFormActivity(reportDeceasedJsonFormString);
                return true;
            case R.id.change_status:
                FragmentTransaction ft = this.getFragmentManager().beginTransaction();
                android.app.Fragment prev = this.getFragmentManager().findFragmentByTag(DIALOG_TAG);
                if (prev != null) {
                    ft.remove(prev);
                }
                StatusEditDialogFragment.newInstance(detailsMap).show(ft, DIALOG_TAG);
                return true;
            case R.id.report_adverse_event:
                return launchAdverseEventForm();
            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void navigateToRegisterActivity() {
        Intent intent = new Intent(getApplicationContext(), ChildRegisterActivity.class);
        intent.putExtra(AllConstants.INTENT_KEY.IS_REMOTE_LOGIN, false);
        startActivity(intent);
        finish();
    }

}
