package org.smartregister.child.sample.cursor;

import android.database.Cursor;

import org.smartregister.child.sample.util.DBConstants;


public class CreateRemoteLocalCursor {
    private String id;
    private String relationalId;
    private String firstName;
    private String lastName;
    private String gender;
    private String dob;
    private String openSrpId;
    private String motherFirstName;
    private String motherLastName;
    private String inactive;
    private String lostToFollowUp;

    private String phoneNumber;
    private String altName;

    public CreateRemoteLocalCursor(Cursor cursor, boolean isRemote) {
        if (isRemote) {
            id = cursor.getString(cursor.getColumnIndex(DBConstants.KEY.ID_LOWER_CASE));
        } else {
            id = cursor.getString(cursor.getColumnIndex(DBConstants.KEY.BASE_ENTITY_ID));
        }
        relationalId = cursor.getString(cursor.getColumnIndex(DBConstants.KEY.RELATIONALID));
        firstName = cursor.getString(cursor.getColumnIndex(DBConstants.KEY.FIRST_NAME));
        lastName = cursor.getString(cursor.getColumnIndex(DBConstants.KEY.LAST_NAME));
        dob = cursor.getString(cursor.getColumnIndex(DBConstants.KEY.DOB));
        openSrpId = cursor.getString(cursor.getColumnIndex(DBConstants.KEY.ZEIR_ID));
        gender = cursor.getString(cursor.getColumnIndex(DBConstants.KEY.GENDER));
        motherFirstName = cursor.getString(cursor.getColumnIndex(DBConstants.KEY.MOTHER_FIRST_NAME));
        motherLastName = cursor.getString(cursor.getColumnIndex(DBConstants.KEY.MOTHER_LAST_NAME));
        inactive = cursor.getString(cursor.getColumnIndex(DBConstants.KEY.INACTIVE));
        lostToFollowUp = cursor.getString(cursor.getColumnIndex(DBConstants.KEY.LOST_TO_FOLLOW_UP));
    }

    public String getId() {
        return id;
    }

    public String getRelationalId() {
        return relationalId;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getGender() {
        return gender;
    }

    public String getDob() {
        return dob;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getAltName() {
        return altName;
    }

    public String getOpenSrpId() {
        return openSrpId;
    }

    public String getMotherFirstName() {
        return motherFirstName;
    }

    public String getMotherLastName() {
        return motherLastName;
    }

    public String getInactive() {
        return inactive;
    }

    public void setInactive(String inactive) {
        this.inactive = inactive;
    }

    public String getLostToFollowUp() {
        return lostToFollowUp;
    }

    public void setLostToFollowUp(String lostToFollowUp) {
        this.lostToFollowUp = lostToFollowUp;
    }
}
