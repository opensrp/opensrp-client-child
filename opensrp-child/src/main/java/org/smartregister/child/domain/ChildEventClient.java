package org.smartregister.child.domain;


import org.smartregister.domain.Client;
import org.smartregister.domain.Event;

/**
 * Created by ndegwamartin on 25/02/2019.
 */
public class ChildEventClient {

    private Event event;
    private Client client;

    public ChildEventClient(Client client, Event event) {
        this.client = client;
        this.event = event;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }
}
