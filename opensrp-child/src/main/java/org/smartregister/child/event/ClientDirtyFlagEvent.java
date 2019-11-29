package org.smartregister.child.event;

/**
 * Created by ndegwamartin on 2019-11-11.
 */
public class ClientDirtyFlagEvent extends BaseEvent {
    private String eventType;
    private String baseEntityId;

    public ClientDirtyFlagEvent(String baseEntityId, String eventType) {

        this.baseEntityId = baseEntityId;
        this.eventType = eventType;
    }

    public String getEventType() {
        return eventType;
    }

    public String getBaseEntityId() {
        return baseEntityId;
    }
}
