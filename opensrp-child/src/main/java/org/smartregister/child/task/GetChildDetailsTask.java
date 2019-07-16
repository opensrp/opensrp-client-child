package org.smartregister.child.task;

import android.os.AsyncTask;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import org.opensrp.api.constants.Gender;
import org.smartregister.CoreLibrary;
import org.smartregister.child.R;
import org.smartregister.child.activity.BaseActivity;
import org.smartregister.child.activity.BaseChildImmunizationActivity;
import org.smartregister.child.util.Constants;
import org.smartregister.child.util.Utils;
import org.smartregister.commonregistry.CommonPersonObject;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.domain.ProfileImage;
import org.smartregister.immunization.util.ImageUtils;
import org.smartregister.repository.DetailsRepository;
import org.smartregister.util.OpenSRPImageLoader;
import org.smartregister.view.activity.DrishtiApplication;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by ndegwamartin on 06/03/2019.
 */
public class GetChildDetailsTask extends AsyncTask<Void, Void, CommonPersonObjectClient> {
    private final String baseEntityId;
    private final BaseActivity baseActivity;
    private final DetailsRepository detailsRepository;
    private final View itemView;

    private ImageView profilePhoto;
    private TextView initials;

    public GetChildDetailsTask(BaseActivity baseActivity, String baseEntityId, DetailsRepository detailsRepository,
                               View itemView) {
        this.baseActivity = baseActivity;
        this.detailsRepository = detailsRepository;
        this.baseEntityId = baseEntityId;
        this.itemView = itemView;
        init();
    }

    private void init() {
        profilePhoto = itemView.findViewById(R.id.profile_photo);
        initials = itemView.findViewById(R.id.initials);
    }

    @Override
    protected CommonPersonObjectClient doInBackground(Void... params) {
        CommonPersonObject rawDetails =
                CoreLibrary.getInstance().context().commonrepository(Utils.metadata().childRegister.tableName)
                        .findByBaseEntityId(baseEntityId);
        if (rawDetails != null) {
            // Get extra child details
            CommonPersonObjectClient childDetails = Utils.convert(rawDetails);
            Utils.putAll(childDetails.getColumnmaps(), detailsRepository.getAllDetailsForClient(baseEntityId));

            // Check if child has a profile pic
            ProfileImage profileImage = CoreLibrary.getInstance().context().imageRepository().findByEntityId(baseEntityId);

            childDetails.getColumnmaps().put("has_profile_image", "true");
            if (profileImage == null) {
                childDetails.getColumnmaps().put("has_profile_image", "false");
            }

            // Get mother details
            String motherBaseEntityId = Utils.getValue(childDetails.getColumnmaps(), "relational_id", false);

            Map<String, String> motherDetails = new HashMap<>();
            motherDetails.put("mother_first_name", "");
            motherDetails.put("mother_last_name", "");
            motherDetails.put("mother_dob", "");
            motherDetails.put("mother_nrc_number", "");
            if (!TextUtils.isEmpty(motherBaseEntityId)) {
                CommonPersonObject rawMotherDetails =
                        CoreLibrary.getInstance().context().commonrepository(Utils.metadata().childRegister.motherTableName)
                                .findByBaseEntityId(motherBaseEntityId);
                if (rawMotherDetails != null) {
                    motherDetails
                            .put("mother_first_name", Utils.getValue(rawMotherDetails.getColumnmaps(), "first_name", false));
                    motherDetails
                            .put("mother_last_name", Utils.getValue(rawMotherDetails.getColumnmaps(), "last_name", false));
                    motherDetails.put("mother_dob", Utils.getValue(rawMotherDetails.getColumnmaps(), "dob", false));
                    motherDetails
                            .put("mother_nrc_number", Utils.getValue(rawMotherDetails.getColumnmaps(), "nrc_number", false));
                }
            }
            Utils.putAll(childDetails.getColumnmaps(), motherDetails);
            childDetails.setDetails(childDetails.getColumnmaps());

            return childDetails;
        }

        return null;
    }

    @Override
    protected void onPostExecute(CommonPersonObjectClient childDetails) {
        super.onPostExecute(childDetails);
        if (childDetails != null) {
            updatePicture(baseActivity, baseEntityId, childDetails);
        }
    }

    private void updatePicture(final BaseActivity baseActivity, String baseEntityId,
                               final CommonPersonObjectClient childDetails) {
        Gender gender = Gender.UNKNOWN;
        int genderColor = R.color.gender_neutral_green;
        int genderLightColor = R.color.gender_neutral_light_green;
        String genderString =
                org.smartregister.util.Utils.getValue(childDetails.getColumnmaps(), Constants.KEY.GENDER, false);

        if (genderString != null && genderString.toLowerCase().equals(Constants.GENDER.FEMALE)) {
            gender = Gender.FEMALE;
            genderColor = R.color.female_pink;
            genderLightColor = R.color.female_light_pink;
        } else if (genderString != null && genderString.toLowerCase().equals(Constants.GENDER.MALE)) {
            gender = Gender.MALE;
            genderColor = R.color.male_blue;
            genderLightColor = R.color.male_light_blue;
        }

        if (org.smartregister.util.Utils.getValue(childDetails.getColumnmaps(), "has_profile_image", false)
                .equals(Constants.TRUE)) {
            profilePhoto.setVisibility(View.VISIBLE);
            initials.setBackgroundColor(baseActivity.getResources().getColor(android.R.color.transparent));
            initials.setTextColor(baseActivity.getResources().getColor(android.R.color.black));
            profilePhoto.setTag(org.smartregister.R.id.entity_id, baseEntityId);
            DrishtiApplication.getCachedImageLoaderInstance().getImageByClientId(baseEntityId, OpenSRPImageLoader
                    .getStaticImageListener(profilePhoto, ImageUtils.profileImageResourceByGender(gender),
                            ImageUtils.profileImageResourceByGender(gender)));
        } else {
            profilePhoto.setVisibility(View.GONE);
            initials.setBackgroundColor(baseActivity.getResources().getColor(genderLightColor));
            initials.setTextColor(baseActivity.getResources().getColor(genderColor));
        }

        final String firstName = org.smartregister.util.Utils.getValue(childDetails.getColumnmaps(), "first_name", true);
        final String lastName = org.smartregister.util.Utils.getValue(childDetails.getColumnmaps(), "last_name", true);

        if (org.smartregister.util.Utils.getValue(childDetails.getColumnmaps(), "has_profile_image", false)
                .equals("false")) {
            initials.setVisibility(View.VISIBLE);
            String initialsString = "";
            if (!TextUtils.isEmpty(firstName)) {
                initialsString = firstName.substring(0, 1);
            }

            if (!TextUtils.isEmpty(lastName)) {
                initialsString = initialsString + lastName.substring(0, 1);
            }

            initials.setText(initialsString.toUpperCase());
        } else {
            initials.setVisibility(View.GONE);
        }

        itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Utils.showToast(baseActivity, firstName + " " + lastName);
                return true;
            }
        });

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BaseChildImmunizationActivity.launchActivity(baseActivity, childDetails, null);
                baseActivity.finish();
            }
        });
    }

}

