package org.smartregister.child.wrapper;

import org.smartregister.growthmonitoring.domain.Weight;

/**
 * Created by ndegwamartin on 04/03/2019.
 */
public class WeightViewRecordUpdateWrapper extends BaseViewRecordUpdateWrapper {

    private Weight weight;

    public Weight getWeight() {
        return weight;
    }

    public void setWeight(Weight weight) {
        this.weight = weight;
    }
}
