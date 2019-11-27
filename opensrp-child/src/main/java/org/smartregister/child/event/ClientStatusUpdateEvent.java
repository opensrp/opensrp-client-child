package org.smartregister.child.event;

/**
 * Created by ndegwamartin on 2019-11-27.
 */
public class ClientStatusUpdateEvent extends BaseEvent {

    private String status;

    public ClientStatusUpdateEvent(String status) {
        this.status = status;
    }
}
