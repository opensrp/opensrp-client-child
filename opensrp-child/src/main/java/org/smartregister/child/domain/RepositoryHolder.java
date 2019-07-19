package org.smartregister.child.domain;

import org.smartregister.commonregistry.CommonRepository;
import org.smartregister.growthmonitoring.repository.HeightRepository;
import org.smartregister.growthmonitoring.repository.WeightRepository;
import org.smartregister.immunization.repository.RecurringServiceRecordRepository;
import org.smartregister.immunization.repository.RecurringServiceTypeRepository;
import org.smartregister.immunization.repository.VaccineRepository;

/**
 * Created by ndegwamartin on 05/03/2019.
 */
public class RepositoryHolder {

    private CommonRepository commonRepository;
    private VaccineRepository vaccineRepository;
    private WeightRepository weightRepository;
    private HeightRepository heightRepository;
    private RecurringServiceRecordRepository recurringServiceRecordRepository;
    private RecurringServiceTypeRepository recurringServiceTypeRepository;

    public RecurringServiceRecordRepository getRecurringServiceRecordRepository() {
        return recurringServiceRecordRepository;
    }

    public void setRecurringServiceRecordRepository(RecurringServiceRecordRepository recurringServiceRecordRepository) {
        this.recurringServiceRecordRepository = recurringServiceRecordRepository;
    }

    public RecurringServiceTypeRepository getRecurringServiceTypeRepository() {
        return recurringServiceTypeRepository;
    }

    public void setRecurringServiceTypeRepository(RecurringServiceTypeRepository recurringServiceTypeRepository) {
        this.recurringServiceTypeRepository = recurringServiceTypeRepository;
    }

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

    public HeightRepository getHeightRepository() {
        return heightRepository;
    }

    public void setHeightRepository(HeightRepository heightRepository) {
        this.heightRepository = heightRepository;
    }
}
