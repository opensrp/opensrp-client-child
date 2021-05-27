package org.smartregister.child.domain;

import org.junit.Test;
import org.smartregister.child.BaseUnitTest;
import org.smartregister.commonregistry.CommonRepository;
import org.smartregister.growthmonitoring.repository.HeightRepository;
import org.smartregister.growthmonitoring.repository.WeightRepository;
import org.smartregister.immunization.repository.RecurringServiceRecordRepository;
import org.smartregister.immunization.repository.RecurringServiceTypeRepository;
import org.smartregister.immunization.repository.VaccineRepository;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.mock;

public class RepositoryHolderTest extends BaseUnitTest {

    private final RepositoryHolder repositoryHolder = new RepositoryHolder();

    @Test
    public void testGetRecurringServiceRecordRepositoryReturnsCorrectValue(){
        RecurringServiceRecordRepository recurringServiceRecordRepository = mock(RecurringServiceRecordRepository.class);
        repositoryHolder.setRecurringServiceRecordRepository(recurringServiceRecordRepository);
        assertThat(repositoryHolder.getRecurringServiceRecordRepository(), is(equalTo(recurringServiceRecordRepository)));
    }

    @Test
    public void testGetRecurringServiceTypeRepositoryReturnsCorrectValue(){
        RecurringServiceTypeRepository recurringServiceTypeRepository = mock(RecurringServiceTypeRepository.class);
        repositoryHolder.setRecurringServiceTypeRepository(recurringServiceTypeRepository);
        assertThat(repositoryHolder.getRecurringServiceTypeRepository(), is(equalTo(recurringServiceTypeRepository)));
    }

    @Test
    public void testGetCommonRepositoryReturnsCorrectValue(){
        CommonRepository commonRepository = mock(CommonRepository.class);
        repositoryHolder.setCommonRepository(commonRepository);
        assertThat(repositoryHolder.getCommonRepository(), is(equalTo(commonRepository)));
    }

    @Test
    public void testGetVaccineRepositoryReturnsCorrectValue(){
        VaccineRepository vaccineRepository = mock(VaccineRepository.class);
        repositoryHolder.setVaccineRepository(vaccineRepository);
        assertThat(repositoryHolder.getVaccineRepository(), is(equalTo(vaccineRepository)));
    }

    @Test
    public void testGetWeightRepositoryReturnsCorrectValue(){
        WeightRepository weightRepository = mock(WeightRepository.class);
        repositoryHolder.setWeightRepository(weightRepository);
        assertThat(repositoryHolder.getWeightRepository(), is(equalTo(weightRepository)));
    }

    @Test
    public void testGetHeightRepositoryReturnsCorrectValue(){
        HeightRepository heightRepository = mock(HeightRepository.class);
        repositoryHolder.setHeightRepository(heightRepository);
        assertThat(repositoryHolder.getHeightRepository(), is(equalTo(heightRepository)));
    }
}
