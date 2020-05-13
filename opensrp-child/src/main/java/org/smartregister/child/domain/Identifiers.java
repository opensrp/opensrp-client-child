package org.smartregister.child.domain;

/**
 * Created by ndegwamartin on 2020-04-22.
 */
public class Identifiers {

    private String providerId;
    private String locationId;
    private String childLocationId;
    private String teamId;
    private String team;

    public String getProviderId() {
        return providerId;
    }

    public void setProviderId(String providerId) {
        this.providerId = providerId;
    }

    public String getLocationId() {
        return locationId;
    }

    public String getChildLocationId() {
        return childLocationId;
    }

    public void setChildLocationId(String childLocationId) {
        this.childLocationId = childLocationId;
    }

    public void setLocationId(String locationId) {
        this.locationId = locationId;
    }

    public String getTeamId() {
        return teamId;
    }

    public void setTeamId(String teamId) {
        this.teamId = teamId;
    }

    public String getTeam() {
        return team;
    }

    public void setTeam(String team) {
        this.team = team;
    }
}
