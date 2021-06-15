package org.smartregister.child.util;

import org.junit.Assert;
import org.junit.Test;
import org.smartregister.child.BaseUnitTest;
import org.smartregister.child.domain.NamedObject;
import org.smartregister.domain.Alert;
import org.smartregister.growthmonitoring.domain.Height;
import org.smartregister.growthmonitoring.domain.Weight;
import org.smartregister.immunization.domain.ServiceRecord;
import org.smartregister.immunization.domain.ServiceType;
import org.smartregister.immunization.domain.Vaccine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AsyncTaskUtilsTest extends BaseUnitTest {

    @Test
    public void testExtractVaccinesWithEmptyMapReturnsEmptyList() {
        Assert.assertEquals(AsyncTaskUtils.extractVaccines(new HashMap<>()), new ArrayList<>());
    }

    @Test
    public void testExtractVaccinesWithNonEmptyMapReturnsVaccineList() {
        List<Vaccine> vaccines = new ArrayList<>();
        NamedObject<List<Vaccine>> namedObject = new NamedObject<>("Vac", vaccines);

        Map<String, NamedObject<?>> map = new HashMap<>();
        map.put(Vaccine.class.getName(), namedObject);

        Assert.assertEquals(AsyncTaskUtils.extractVaccines(map), vaccines);
    }

    @Test
    public void testExtractServiceTypesWithEmptyMapReturnsEmptyHashMap() {
        Assert.assertEquals(AsyncTaskUtils.extractServiceTypes(new HashMap<>()), new HashMap<>());
    }

    @Test
    public void testExtractServiceTypesWithNonEmptyMapReturnsVaccineList() {
        Map<String, List<ServiceType>> serviceTypes = new HashMap<>();
        List<ServiceType> list = new ArrayList<>();
        serviceTypes.put(ServiceType.class.getName(), list);

        NamedObject<Map<String, List<ServiceType>>> namedObject = new NamedObject<>("Growth & Monitoring", serviceTypes);

        Map<String, NamedObject<?>> map = new HashMap<>();
        map.put(ServiceType.class.getName(), namedObject);

        Assert.assertEquals(AsyncTaskUtils.extractServiceTypes(map), serviceTypes);
    }

    @Test
    public void testExtractServiceRecordsWithEmptyMapReturnsEmptyList() {
        Assert.assertEquals(AsyncTaskUtils.extractServiceRecords(new HashMap<>()), new ArrayList<>());
    }

    @Test
    public void testExtractServiceRecordsWithNonEmptyMapReturnsServiceRecordsList() {
        List<ServiceRecord> serviceRecords = new ArrayList<>();
        NamedObject<List<ServiceRecord>> namedObject = new NamedObject<>("Record", serviceRecords);

        Map<String, NamedObject<?>> map = new HashMap<>();
        map.put(ServiceRecord.class.getName(), namedObject);

        Assert.assertEquals(AsyncTaskUtils.extractServiceRecords(map), serviceRecords);
    }

    @Test
    public void testExtractAlertsWithEmptyMapReturnsEmptyList() {
        Assert.assertEquals(AsyncTaskUtils.extractAlerts(new HashMap<>()), new ArrayList<>());
    }

    @Test
    public void testExtractAlertsWithNonEmptyMapReturnsAlertsList() {
        List<Alert> alerts = new ArrayList<>();
        NamedObject<List<Alert>> namedObject = new NamedObject<>("Alerts", alerts);

        Map<String, NamedObject<?>> map = new HashMap<>();
        map.put(Alert.class.getName(), namedObject);

        Assert.assertEquals(AsyncTaskUtils.extractAlerts(map), alerts);
    }

    @Test
    public void testRetrieveWeightWithEmptyMapReturnsNull() {
        Assert.assertNull(AsyncTaskUtils.retrieveWeight(new HashMap<>()));
    }

    @Test
    public void testRetrieveWeightWithNonEmptyMapReturnsWeightObject() {
        Weight weight = new Weight();
        NamedObject<Weight> namedObject = new NamedObject<>("Weight", weight);

        Map<String, NamedObject<?>> map = new HashMap<>();
        map.put(Weight.class.getName(), namedObject);

        Assert.assertEquals(AsyncTaskUtils.retrieveWeight(map), weight);
    }

    @Test
    public void testRetrieveHeightWithEmptyMapReturnsNull() {
        Assert.assertNull(AsyncTaskUtils.retrieveHeight(new HashMap<>()));
    }

    @Test
    public void testRetrieveHeightWithNonEmptyMapReturnsWeightObject() {
        Height height = new Height();
        NamedObject<Height> namedObject = new NamedObject<>("Height", height);

        Map<String, NamedObject<?>> map = new HashMap<>();
        map.put(Height.class.getName(), namedObject);

        Assert.assertEquals(AsyncTaskUtils.retrieveHeight(map), height);
    }

    @Test
    public void testExtractWeightsWithEmptyMapReturnsEmptyList() {
        Assert.assertEquals(AsyncTaskUtils.extractWeights(new HashMap<>()), new ArrayList<>());
    }

    @Test
    public void testExtractWeightsWithNonEmptyMapReturnsVaccineList() {
        List<Weight> heights = new ArrayList<>();
        NamedObject<List<Weight>> namedObject = new NamedObject<>("Weight", heights);

        Map<String, NamedObject<?>> map = new HashMap<>();
        map.put(Weight.class.getName(), namedObject);

        Assert.assertEquals(AsyncTaskUtils.extractWeights(map), heights);
    }

    @Test
    public void testExtractHeightsWithEmptyMapReturnsEmptyList() {
        Assert.assertEquals(AsyncTaskUtils.extractHeights(new HashMap<>()), new ArrayList<>());
    }

    @Test
    public void testExtractHeightsWithNonEmptyMapReturnsVaccineList() {
        List<Height> heights = new ArrayList<>();
        NamedObject<List<Height>> namedObject = new NamedObject<>("Heights", heights);

        Map<String, NamedObject<?>> map = new HashMap<>();
        map.put(Height.class.getName(), namedObject);

        Assert.assertEquals(AsyncTaskUtils.extractHeights(map), heights);
    }

    @Test
    public void testExtractDetailsMapWithEmptyMapReturnsEmptyHashMap() {
        Assert.assertEquals(AsyncTaskUtils.extractDetailsMap(new HashMap<>()), new HashMap<>());
    }

    @Test
    public void testExtractDetailsMapWithNonEmptyMapReturnsMapOfDetails() {
        Map<String, String> entries = new HashMap<>();
        NamedObject<Map<String, String>> namedObject = new NamedObject<>("Details", entries);

        Map<String, NamedObject<?>> map = new HashMap<>();
        map.put(Map.class.getName(), namedObject);

        Assert.assertEquals(AsyncTaskUtils.extractDetailsMap(map), entries);
    }
}
