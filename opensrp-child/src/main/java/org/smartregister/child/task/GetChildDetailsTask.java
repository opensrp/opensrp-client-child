package org.smartregister.child.task;

import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import org.opensrp.api.constants.Gender;
import org.smartregister.CoreLibrary;
import org.smartregister.child.R;
import org.smartregister.child.activity.BaseActivity;
import org.smartregister.child.activity.BaseChildImmunizationActivity;
import org.smartregister.child.util.ChildDbUtils;
import org.smartregister.child.util.Constants;
import org.smartregister.child.util.Utils;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.domain.ProfileImage;
import org.smartregister.immunization.util.ImageUtils;
import org.smartregister.util.AppExecutorService;
import org.smartregister.util.OpenSRPImageLoader;
import org.smartregister.view.activity.DrishtiApplication;

import java.util.Locale;

import timber.log.Timber;

/**
 * Created by ndegwamartin on 06/03/2019.
 */
public class GetChildDetailsTask implements OnTaskExecutedActions<CommonPersonObjectClient> {
    private final String baseEntityId;
    private final BaseActivity baseActivity;
    private final View itemView;
    private ImageView profilePhoto;
    private TextView initials;
    private AppExecutorService appExecutors;

    public GetChildDetailsTask(BaseActivity baseActivity, String baseEntityId, View itemView) {
        this.baseActivity = baseActivity;
        this.baseEntityId = baseEntityId;
        this.itemView = itemView;

        init();
    }

    private void init() {
        profilePhoto = itemView.findViewById(R.id.profile_photo);
        initials = itemView.findViewById(R.id.initials);
    }

    @Override
    public void onTaskStarted() {
        // do nothing
    }

    @Override
    public void execute() {
        appExecutors = new AppExecutorService();
        appExecutors.executorService().execute(() -> {
            CommonPersonObjectClient childDetails = getChildDetails();

            appExecutors.mainThread().execute(() -> onTaskResult(childDetails));
        });
    }

    @Override
    public void onTaskResult(CommonPersonObjectClient childDetails) {
        if (childDetails != null) {
            updatePicture(baseActivity, baseEntityId, childDetails);
        }
    }

    private CommonPersonObjectClient getChildDetails() {
        CommonPersonObjectClient childDetails = ChildDbUtils.fetchCommonPersonObjectClientByBaseEntityId(baseEntityId);

        try {
            // Check if child has a profile pic
            ProfileImage profileImage = CoreLibrary.getInstance().context().imageRepository().findByEntityId(baseEntityId);
            if (profileImage == null) {
                childDetails.getColumnmaps().put(Constants.KEY.HAS_PROFILE_IMAGE, Constants.FALSE);
            } else {
                childDetails.getColumnmaps().put(Constants.KEY.HAS_PROFILE_IMAGE, Constants.TRUE);
            }
        } catch (Exception e) {
            Timber.e(e);
        }

        return childDetails;
    }

    private void updatePicture(final BaseActivity baseActivity, String baseEntityId, final CommonPersonObjectClient childDetails) {
        Gender gender = Gender.UNKNOWN;
        int genderColor = R.color.gender_neutral_green;
        int genderLightColor = R.color.gender_neutral_light_green;
        boolean isDeceased = !TextUtils.isEmpty(childDetails.getColumnmaps().get(Constants.KEY.DOD));

        String genderString = org.smartregister.util.Utils.getValue(childDetails.getColumnmaps(), Constants.KEY.GENDER, false);
        if (genderString != null && genderString.toLowerCase().equals(Constants.GENDER.FEMALE)) {
            gender = Gender.FEMALE;
            genderColor = isDeceased ? R.color.dark_grey : R.color.female_pink;
            genderLightColor = isDeceased ? R.color.lighter_grey : R.color.female_light_pink;
        } else if (genderString != null && genderString.toLowerCase().equals(Constants.GENDER.MALE)) {
            gender = Gender.MALE;
            genderColor = isDeceased ? R.color.dark_grey : R.color.male_blue;
            genderLightColor = isDeceased ? R.color.lighter_grey : R.color.male_light_blue;
        }

        if (org.smartregister.util.Utils.getValue(childDetails.getColumnmaps(), Constants.KEY.HAS_PROFILE_IMAGE, false).equals(Constants.TRUE)) {
            profilePhoto.setVisibility(View.VISIBLE);
            initials.setBackgroundColor(baseActivity.getResources().getColor(android.R.color.transparent));
            initials.setTextColor(baseActivity.getResources().getColor(android.R.color.black));
            profilePhoto.setTag(org.smartregister.R.id.entity_id, baseEntityId);
            DrishtiApplication.getCachedImageLoaderInstance().getImageByClientId(
                    baseEntityId,
                    OpenSRPImageLoader.getStaticImageListener(
                            profilePhoto,
                            ImageUtils.profileImageResourceByGender(gender),
                            ImageUtils.profileImageResourceByGender(gender)
                    )
            );
        } else {
            profilePhoto.setVisibility(View.GONE);
            initials.setBackgroundColor(baseActivity.getResources().getColor(genderLightColor));
            initials.setTextColor(baseActivity.getResources().getColor(genderColor));
        }

        processChildNames(baseActivity, childDetails);
    }

    private void processChildNames(final BaseActivity baseActivity, final CommonPersonObjectClient childDetails) {
        final String firstName = org.smartregister.util.Utils.getValue(childDetails.getColumnmaps(), Constants.KEY.FIRST_NAME, true);
        final String lastName = org.smartregister.util.Utils.getValue(childDetails.getColumnmaps(), Constants.KEY.LAST_NAME, true);

        if (org.smartregister.util.Utils.getValue(childDetails.getColumnmaps(), "has_profile_image", false).equals(Constants.FALSE)) {
            initials.setVisibility(View.VISIBLE);
            String initialsString = "";
            if (!TextUtils.isEmpty(firstName)) {
                initialsString = firstName.substring(0, 1);
            }

            if (!TextUtils.isEmpty(lastName)) {
                initialsString = initialsString + lastName.charAt(0);
            }

            initials.setText(initialsString.toUpperCase());
        } else {
            initials.setVisibility(View.GONE);
        }

        itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Utils.showToast(baseActivity, String.format(Locale.getDefault(), baseActivity.getString(R.string.formatted_space_separated), firstName, lastName));
                return true;
            }
        });

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(childDetails.getColumnmaps().get(Constants.KEY.DOD))) {
                    Utils.showToast(baseActivity, baseActivity.getResources().getString(R.string.marked_as_deceased, String.format(baseActivity.getResources().getString(R.string.formatted_space_separated), firstName, lastName)));
                } else {
                    BaseChildImmunizationActivity.launchActivity(baseActivity, childDetails, null);
                    baseActivity.finish();
                }
            }
        });
    }
}
