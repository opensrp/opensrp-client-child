package org.smartregister.child.domain;

import org.smartregister.commonregistry.CommonRepository;
import org.smartregister.growthmonitoring.repository.WeightRepository;
import org.smartregister.immunization.repository.VaccineRepository;

/**
 * Created by ndegwamartin on 05/03/2019.
 */
public class RepositoryHolder {

    private CommonRepository commonRepository;
    private VaccineRepository vaccineRepository;
    private WeightRepository weightRepository;

    public CommonRepository getCommonRepository() {
        return commonRepository;
    }

    public void setCommonRepository(CommonRepository commonRepository) {
        this.commonRepository = commonRepository;
    }

    public VaccineRepository getVaccineRepository() {
        return vaccineRepository;
    }

    public void setVaccineRepository(VaccineRepository vaccineRepository) {
        this.vaccineRepository = vaccineRepository;
    }

    public WeightRepository getWeightRepository() {
        return weightRepository;
    }

    public void setWeightRepository(WeightRepository weightRepository) {
        this.weightRepository = weightRepository;
    }
}
