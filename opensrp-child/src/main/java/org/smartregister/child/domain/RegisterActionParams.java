package org.smartregister.child.domain;

import android.view.View;

import org.smartregister.view.contract.SmartRegisterClient;

/**
 * Created by ndegwamartin on 05/03/2019.
 */
public class RegisterActionParams {
    private View convertView;
    private View profileInfoView;
    private String entityId;
    private String dobString;
    private String lostToFollowUp;
    private String inactive;
    private SmartRegisterClient smartRegisterClient;
    private View.OnClickListener onClickListener;

    private Boolean updateOutOfCatchment;

    public View getConvertView() {
        return convertView;
    }

    public void setConvertView(View convertView) {
        this.convertView = convertView;
    }

    public String getEntityId() {
        return entityId;
    }

    public void setEntityId(String entityId) {
        this.entityId = entityId;
    }

    public String getDobString() {
        return dobString;
    }

    public void setDobString(String dobString) {
        this.dobString = dobString;
    }

    public String getLostToFollowUp() {
        return lostToFollowUp;
    }

    public void setLostToFollowUp(String lostToFollowUp) {
        this.lostToFollowUp = lostToFollowUp;
    }

    public String getInactive() {
        return inactive;
    }

    public void setInactive(String inactive) {
        this.inactive = inactive;
    }

    public SmartRegisterClient getSmartRegisterClient() {
        return smartRegisterClient;
    }

    public void setSmartRegisterClient(SmartRegisterClient smartRegisterClient) {
        this.smartRegisterClient = smartRegisterClient;
    }

    public Boolean getUpdateOutOfCatchment() {
        return updateOutOfCatchment;
    }

    public void setUpdateOutOfCatchment(Boolean updateOutOfCatchment) {
        this.updateOutOfCatchment = updateOutOfCatchment;
    }

    public View.OnClickListener getOnClickListener() {
        return onClickListener;
    }

    public void setOnClickListener(View.OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    public View getProfileInfoView() {
        return profileInfoView;
    }

    public void setProfileInfoView(View profileInfoView) {
        this.profileInfoView = profileInfoView;
    }
}
