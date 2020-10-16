package org.smartregister.child.domain;

import org.json.JSONObject;

/**
 * Created by ndegwamartin on 23/09/2020.
 */
public class MoveToCatchmentEvent {
    private JSONObject jsonObject;
    private boolean permanent;
    private boolean createEvent;
    private boolean migrateTemporary;

    public MoveToCatchmentEvent(JSONObject jsonObject, boolean permanent, boolean createEvent) {
        this.jsonObject = jsonObject;
        this.permanent = permanent;
        this.createEvent = createEvent;
    }

    public JSONObject getJsonObject() {
        return jsonObject;
    }

    public boolean isPermanent() {
        return permanent;
    }

    public boolean isCreateEvent() {
        return createEvent;
    }

    public void setMigrateTemporary(boolean migrateTemporary) {
        this.migrateTemporary = migrateTemporary;
    }

    public boolean isMigrateTemporary() {
        return migrateTemporary;
    }
}
