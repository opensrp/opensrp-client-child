package org.smartregister.child.util;

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

/**
 * Created by samuelgithengi on 3/2/18.
 */

public class AsyncTaskUtils {

    public static List<Vaccine> extractVaccines(Map<String, NamedObject<?>> map) {
        if (map.containsKey(Vaccine.class.getName())) {
            NamedObject<?> namedObject = map.get(Vaccine.class.getName());
            if (namedObject != null) {
                return (List<Vaccine>) namedObject.object;
            }
        }
        return new ArrayList<>();
    }
    
    public static Map<String, List<ServiceType>> extractServiceTypes(Map<String, NamedObject<?>> map) {
        if (map.containsKey(ServiceType.class.getName())) {
            NamedObject<?> namedObject = map.get(ServiceType.class.getName());
            if (namedObject != null) {
                return (Map<String, List<ServiceType>>) namedObject.object;
            }
        }
        return new HashMap<>();
    }

    public static List<ServiceRecord> extractServiceRecords(Map<String, NamedObject<?>> map) {
        if (map.containsKey(ServiceRecord.class.getName())) {
            NamedObject<?> namedObject = map.get(ServiceRecord.class.getName());
            if (namedObject != null) {
                return (List<ServiceRecord>) namedObject.object;
            }
        }
        return new ArrayList<>();
    }

    public static List<Alert> extractAlerts(Map<String, NamedObject<?>> map) {
        if (map.containsKey(Alert.class.getName())) {
            NamedObject<?> namedObject = map.get(Alert.class.getName());
            if (namedObject != null) {
                return (List<Alert>) namedObject.object;
            }
        }
        return new ArrayList<>();
    }

    public static Weight retrieveWeight(Map<String, NamedObject<?>> map) {
        if (map.containsKey(Weight.class.getName())) {
            NamedObject<?> namedObject = map.get(Weight.class.getName());
            if (namedObject != null) {
                return (Weight) namedObject.object;
            }
        }
        return null;
    }

    public static Height retrieveHeight(Map<String, NamedObject<?>> map) {
        if (map.containsKey(Height.class.getName())) {
            NamedObject<?> namedObject = map.get(Height.class.getName());
            if (namedObject != null) {
                return (Height) namedObject.object;
            }
        }
        return null;
    }

    public static List<Weight> extractWeights(Map<String, NamedObject<?>> map) {
        if (map.containsKey(Weight.class.getName())) {
            NamedObject<?> namedObject = map.get(Weight.class.getName());
            if (namedObject != null) {
                return (List<Weight>) namedObject.object;
            }
        }
        return new ArrayList<>();
    }

    public static List<Height> extractHeights(Map<String, NamedObject<?>> map) {
        if (map.containsKey(Height.class.getName())) {
            NamedObject<?> namedObject = map.get(Height.class.getName());
            if (namedObject != null) {
                return (List<Height>) namedObject.object;
            }
        }
        return new ArrayList<>();
    }

    public static Map<String, String> extractDetailsMap(Map<String, NamedObject<?>> map) {
        if (map.containsKey(Map.class.getName())) {
            NamedObject<?> namedObject = map.get(Map.class.getName());
            if (namedObject != null) {
                return (Map<String, String>) namedObject.object;
            }
        }
        return new HashMap<>();
    }
}
