package org.smartregister.child.domain;

import java.io.Serializable;

/**
 * Created by keyman on 24/02/2017.
 */
public class RegisterClickables implements Serializable {

    private String nextAppointmentDate;

    private boolean recordWeight;

    private boolean recordAll;

    public void setRecordWeight(boolean recordWeight) {
        this.recordWeight = recordWeight;
    }

    public boolean isRecordWeight() {
        return recordWeight;
    }

    public void setRecordAll(boolean recordAll) {
        this.recordAll = recordAll;
    }

    public boolean isRecordAll() {
        return recordAll;
    }

    public String getNextAppointmentDate() {
        return nextAppointmentDate;
    }

    public void setNextAppointmentDate(String nextAppointmentDate) {
        this.nextAppointmentDate = nextAppointmentDate;
    }
}
