package org.smartregister.child.domain;

import static org.mockito.Mockito.mock;

import org.junit.Assert;
import org.junit.Test;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.smartregister.child.BaseUnitTest;
import org.smartregister.commonregistry.CommonRepository;
import org.smartregister.growthmonitoring.repository.HeightRepository;
import org.smartregister.growthmonitoring.repository.WeightRepository;
import org.smartregister.immunization.repository.RecurringServiceRecordRepository;
import org.smartregister.immunization.repository.RecurringServiceTypeRepository;
import org.smartregister.immunization.repository.VaccineRepository;

@PowerMockIgnore({"org.mockito.*", "org.robolectric.*", "android.*", "androidx.*", "javax.management.*", "org.xmlpull.v1.*",})
public class RepositoryHolderTest extends BaseUnitTest {

    private final RepositoryHolder repositoryHolder = new RepositoryHolder();

    @Test
    public void testGetRecurringServiceRecordRepositoryReturnsCorrectValue() {
        RecurringServiceRecordRepository recurringServiceRecordRepository = mock(RecurringServiceRecordRepository.class);
        repositoryHolder.setRecurringServiceRecordRepository(recurringServiceRecordRepository);
        Assert.assertEquals(repositoryHolder.getRecurringServiceRecordRepository(), recurringServiceRecordRepository);
    }

    @Test
    public void testGetRecurringServiceTypeRepositoryReturnsCorrectValue() {
        RecurringServiceTypeRepository recurringServiceTypeRepository = mock(RecurringServiceTypeRepository.class);
        repositoryHolder.setRecurringServiceTypeRepository(recurringServiceTypeRepository);
        Assert.assertEquals(repositoryHolder.getRecurringServiceTypeRepository(), recurringServiceTypeRepository);
    }

    @Test
    public void testGetCommonRepositoryReturnsCorrectValue() {
        CommonRepository commonRepository = mock(CommonRepository.class);
        repositoryHolder.setCommonRepository(commonRepository);
        Assert.assertEquals(repositoryHolder.getCommonRepository(), commonRepository);
    }

    @Test
    public void testGetVaccineRepositoryReturnsCorrectValue() {
        VaccineRepository vaccineRepository = mock(VaccineRepository.class);
        repositoryHolder.setVaccineRepository(vaccineRepository);
        Assert.assertEquals(repositoryHolder.getVaccineRepository(), vaccineRepository);
    }

    @Test
    public void testGetWeightRepositoryReturnsCorrectValue() {
        WeightRepository weightRepository = mock(WeightRepository.class);
        repositoryHolder.setWeightRepository(weightRepository);
        Assert.assertEquals(repositoryHolder.getWeightRepository(), weightRepository);
    }

    @Test
    public void testGetHeightRepositoryReturnsCorrectValue() {
        HeightRepository heightRepository = mock(HeightRepository.class);
        repositoryHolder.setHeightRepository(heightRepository);
        Assert.assertEquals(repositoryHolder.getHeightRepository(), heightRepository);
    }
}
