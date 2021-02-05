package org.smartregister.child.domain;

import org.smartregister.domain.Photo;

/**
 * Created by ndegwamartin on 01/12/2020.
 */
public class WrapperParam {
    private long position;
    private String childName;
    private String gender;
    private String openSrpId;
    private String duration;
    private Photo photo;
    private String pmtctStatus;
    private String baseEntityId;

    public String getBaseEntityId() {
        return baseEntityId;
    }

    public void setBaseEntityId(String baseEntityId) {
        this.baseEntityId = baseEntityId;
    }

    public long getPosition() {
        return position;
    }

    public void setPosition(long position) {
        this.position = position;
    }

    public String getChildName() {
        return childName;
    }

    public void setChildName(String childName) {
        this.childName = childName;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getOpenSrpId() {
        return openSrpId;
    }

    public void setOpenSrpId(String openSrpId) {
        this.openSrpId = openSrpId;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public Photo getPhoto() {
        return photo;
    }

    public void setPhoto(Photo photo) {
        this.photo = photo;
    }

    public String getPmtctStatus() {
        return pmtctStatus;
    }

    public void setPmtctStatus(String pmtctStatus) {
        this.pmtctStatus = pmtctStatus;
    }
}
