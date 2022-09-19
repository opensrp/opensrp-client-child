package org.smartregister.child.util;

import org.smartregister.immunization.ImmunizationLibrary;
import org.smartregister.immunization.repository.VaccineOverdueCountRepository;

/**
 * Helper class with migration script for the new Overdue counter implementation (MIGRATE_VACCINES_QUERY)
 * plus the query to efficiently count the overdue alerts (COUNT_QUERY_SQL)
 */
public class VaccineOverdueCountRepositoryHelper {

    public static final String MIGRATE_VACCINES_QUERY = "INSERT INTO vaccine_overdue_count SELECT DISTINCT ec_child_details.id FROM alerts a INNER JOIN ec_child_details ON a.caseID = ec_child_details.id INNER JOIN ec_client ON a.caseID = ec_client.base_entity_id \n" +
            "WHERE (ec_client.dod IS NULL AND ec_client.date_removed IS NULL AND ec_client.is_closed IS NOT '1' AND ec_child_details.is_closed IS NOT '1') \n" +
            "AND  ( ec_child_details.inactive IS NULL OR ec_child_details.inactive != 'true' )  AND  ( ec_child_details.lost_to_follow_up IS NULL \n" +
            "OR ec_child_details.lost_to_follow_up != 'true' ) AND status ='urgent' AND  ( ec_child_details.lost_to_follow_up IS NULL \n" +
            "OR ec_child_details.lost_to_follow_up != 'true' )  " +
            "AND  a.scheduleName in (  \"OPV_0\",\"BCG\",\"OPV_1\",\"PENTA_1\",\"PCV_1 \",\"ROTA_1\",\"OPV_2\",\"PENTA_2\",\"PCV_2\",\"ROTA_2\",\"OPV_3\",\"PCV_3\",\"PENTA_3\",\"IPV\",\"MR_1\",\"OPV_4\",\"MR_2\"," +
            "\"OPV 0\",\"OPV 1\",\"PENTA 1\",\"PCV 1 \",\"ROTA 1\",\"OPV 2\",\"PENTA 2\",\"PCV 2\",\"ROTA 2\",\"OPV 3\",\"PCV 3\",\"PENTA 3\",\"IPV\",\"MR 1\",\"OPV 4\",\"MR 2\" )\n";

    public static final String COUNT_QUERY_SQL = "SELECT COUNT(1) FROM " + VaccineOverdueCountRepository.TABLE_NAME + " ci INNER JOIN ec_client ON ec_client.id = ci.base_entity_id " +
            "INNER JOIN ec_child_details ON ec_client.id = ec_child_details.id " +
            "WHERE (ec_client.dod IS NULL AND ec_client.date_removed is null AND ec_client.is_closed IS NOT '1' AND ec_client.is_closed IS NOT '1') " +
            "AND  ( ec_child_details.inactive IS NULL OR ec_child_details.inactive != 'true');";

    public static int getOverdueCount() {
        return ImmunizationLibrary.getInstance().getVaccineOverdueCountRepository().getOverdueCount(COUNT_QUERY_SQL);
    }
}
