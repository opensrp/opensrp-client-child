package org.smartregister.child.domain;

public class ExtraVaccineUpdateEvent {

    private final String entityId;
    private final String vaccine;
    private final String vaccineDate;
    private final boolean removed;

    public ExtraVaccineUpdateEvent(String entityId, String vaccine, String vaccineDate) {
        this(entityId, vaccine, vaccineDate, false);
    }

    public ExtraVaccineUpdateEvent(String entityId, String vaccine, String vaccineDate, boolean removed) {
        this.entityId = entityId;
        this.vaccine = vaccine;
        this.vaccineDate = vaccineDate;
        this.removed = removed;
    }

    public String getVaccine() {
        return vaccine;
    }

    public String getVaccineDate() {
        return vaccineDate;
    }

    public boolean isRemoved() {
        return removed;
    }

    public String getEntityId() {
        return entityId;
    }

}
