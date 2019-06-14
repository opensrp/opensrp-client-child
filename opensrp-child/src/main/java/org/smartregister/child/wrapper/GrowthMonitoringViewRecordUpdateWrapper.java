package org.smartregister.child.wrapper;

import org.smartregister.growthmonitoring.domain.Height;
import org.smartregister.growthmonitoring.domain.Weight;

/**
 * Created by ndegwamartin on 04/03/2019.
 */
public class GrowthMonitoringViewRecordUpdateWrapper extends BaseViewRecordUpdateWrapper {

    private Weight weight;
    private Height height;

    public Weight getWeight() {
        return weight;
    }

    public void setWeight(Weight weight) {
        this.weight = weight;
    }

    public Height getHeight() {
        return height;
    }

    public void setHeight(Height height) {
        this.height = height;
    }
}
