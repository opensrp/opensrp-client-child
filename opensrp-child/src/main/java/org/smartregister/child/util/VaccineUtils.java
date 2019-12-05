package org.smartregister.child.util;

import android.support.annotation.NonNull;
import android.text.TextUtils;

import org.smartregister.child.ChildLibrary;
import org.smartregister.child.domain.GroupVaccineCount;
import org.smartregister.immunization.db.VaccineRepo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 * Created by ndegwamartin on 2019-12-05.
 * <p>
 * To Do move to immunization library
 */
public class VaccineUtils {

    public static Map<String, GroupVaccineCount> generateGroupVaccineMap() {

        Map<String, GroupVaccineCount> groupVaccineMap = new HashMap<>();

        ArrayList<VaccineRepo.Vaccine> childVaccineRepo = ChildLibrary.getInstance().cache().childVaccineRepo;
        VaccineRepo.Vaccine repoVaccine;
        String repoGroup;

        for (int i = 0; i < childVaccineRepo.size(); i++) {
            repoVaccine = childVaccineRepo.get(i);
            repoGroup = getGroupName(repoVaccine);

            if (TextUtils.isEmpty(repoGroup)) {
                continue;
            }
            GroupVaccineCount groupVaccineCount = groupVaccineMap.get(repoGroup);
            if (groupVaccineCount == null) {
                groupVaccineCount = new GroupVaccineCount(0, 0);
            }

            groupVaccineCount.setGiven(groupVaccineCount.getGiven() + 1);
            groupVaccineCount.setRemaining(groupVaccineCount.getRemaining() + 1);

            groupVaccineMap.put(repoGroup, groupVaccineCount);

        }

        return groupVaccineMap;
    }


    @NonNull
    public static String getGroupName(VaccineRepo.Vaccine vaccine) {
        if (vaccine != null) {

            String groupName = ChildLibrary.getInstance().cache().reverseLookupGroupMap.get(vaccine.name().toLowerCase(Locale.ENGLISH));
            if (groupName != null) {
                return groupName;
            }
        }

        return "";
    }

    public static ArrayList<VaccineRepo.Vaccine> generateActualVaccines(Set<String> actualChildVaccines) {

        Iterator<VaccineRepo.Vaccine> i = VaccineRepo.getVaccines(Constants.CHILD_TYPE).iterator();
        while (i.hasNext()) {
            VaccineRepo.Vaccine vaccine = i.next();

            if (!actualChildVaccines.contains(vaccine.name())) {
                i.remove();
            }
        }

        return null;
    }
}
