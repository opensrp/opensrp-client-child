package org.smartregister.child;

import org.smartregister.child.domain.GroupVaccineCount;
import org.smartregister.immunization.db.VaccineRepo;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by ndegwamartin on 2019-12-05.
 */
public class Cache {
    public Map<String, String> reverseLookupGroupMap;
    public ArrayList<VaccineRepo.Vaccine> childVaccineRepo;
    public Map<String, GroupVaccineCount> groupVaccineMap;
}
