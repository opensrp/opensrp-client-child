package org.smartregister.child.dao;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Triple;
import org.smartregister.child.util.Constants;
import org.smartregister.dao.AbstractDao;

import java.util.ArrayList;
import java.util.List;

public class ChildDao extends AbstractDao {

    /**
     * Retrieve the selected vaccines that are comma separated and create new list
     *
     * @param tableName table's name
     * @param entityId  unique id for the client (in this case the base_entity_id that is processed from
     *                  events is the form submission id to avoid conflicts with data insertion
     * @return a list of selected vaccines with their dates
     */
    public static List<Triple<String, String, String>> getChildExtraVaccines(String tableName, String entityId) {
        List<Triple<String, String, String>> extraVaccines = getChildExtraVaccines(tableName,
                Triple.of(Constants.KEY.BASE_ENTITY_ID, Constants.KEY.SELECTED_VACCINES, Constants.KEY.VACCINE_DATE), entityId);
        List<Triple<String, String, String>> vaccinesList = new ArrayList<>();

        for (Triple<String, String, String> extraVaccine : extraVaccines) {

            String baseEntityId = extraVaccine.getLeft();
            String vaccineNames = extraVaccine.getMiddle();
            String serviceDate = extraVaccine.getRight();

            if (StringUtils.isNotBlank(vaccineNames) && StringUtils.isNotBlank(serviceDate)) {
                String[] vaccines = vaccineNames.split(",");
                for (String vaccine : vaccines) {

                    vaccinesList.add(Triple.of(baseEntityId, vaccine, serviceDate));
                }
            }
        }

        return vaccinesList;
    }

    private static List<Triple<String, String, String>> getChildExtraVaccines(String tableName, final Triple<String, String, String> columns, String entityId) {
        String query = String.format("SELECT %s, %s, %s FROM %s WHERE %s = '%s' AND is_closed IS 0",
                columns.getLeft(), columns.getMiddle(), columns.getRight(), tableName, Constants.KEY.ENTITY_ID, entityId);
        DataMap<Triple<String, String, String>> dataMap = cursor ->
                Triple.of(getCursorValue(cursor, columns.getLeft()), getCursorValue(cursor, columns.getMiddle()),
                        getCursorValue(cursor, columns.getRight()));

        List<Triple<String, String, String>> result = readData(query, dataMap);
        if (result == null) return new ArrayList<>();
        return result;
    }

    /**
     * Return list of available recurring services
     *
     * @return recurring services
     */
    public static List<String> getRecurringServiceTypes() {
        String query = "SELECT DISTINCT type FROM recurring_service_types";

        DataMap<String> dataMap = cursor -> getCursorValue(cursor, "type");

        List<String> result = readData(query, dataMap);
        if (result == null) return new ArrayList<>();
        return result;
    }
}
