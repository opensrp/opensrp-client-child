package org.smartregister.child.dao;

import org.apache.commons.lang3.StringUtils;
import org.smartregister.child.util.Constants;
import org.smartregister.dao.AbstractDao;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ChildDao extends AbstractDao {

    public static List<Map.Entry<String, String>> getChildExtraVaccines(String tableName, final Map.Entry<String, String> entry,
                                                                        String baseEntityColumn, String baseEntityId) {
        String query = String.format("SELECT %s,%s FROM %s WHERE %s = '%s'", entry.getKey(), entry.getValue(), tableName,
                baseEntityColumn, baseEntityId);
        DataMap<Map.Entry<String, String>> dataMap = cursor ->
                new AbstractMap.SimpleEntry<>(getCursorValue(cursor, entry.getKey()),
                        getCursorValue(cursor, entry.getValue()));

        List<Map.Entry<String, String>> result = readData(query, dataMap);
        if (result == null) return new ArrayList<>();
        return result;
    }

    /**
     * Retrieve the selected vaccines that are comma separated and create new list
     *
     * @param baseEntityId unique id for the client
     * @return a list of selected vaccines with their dates
     */
    public static List<Map.Entry<String, String>> getChildExtraVaccines(String baseEntityId) {
        List<Map.Entry<String, String>> extraVaccines = getChildExtraVaccines(Constants.Tables.EC_DYNAMIC_VACCINES,
                new AbstractMap.SimpleEntry<>(Constants.KEY.SELECTED_VACCINES, Constants.KEY.VACCINE_DATE), Constants.KEY.ENTITY_ID, baseEntityId);
        List<Map.Entry<String, String>> vaccinesList = new ArrayList<>();
        for (Map.Entry<String, String> vaccine : extraVaccines) {
            if (StringUtils.isNotBlank(vaccine.getKey()) && StringUtils.isNotBlank(vaccine.getValue())) {
                String[] vaccines = vaccine.getKey().split(",");
                for (String vac : vaccines) {
                    vaccinesList.add(new AbstractMap.SimpleEntry<>(vac, vaccine.getValue()));
                }
            }
        }
        return vaccinesList;
    }

    /**
     * Return list of available recurring services
     * @return recurring services
     */
    public static List<String> getRecurringServiceTypes(){
        String query = "SELECT DISTINCT type FROM recurring_service_types ORDER BY type;";

        DataMap<String> dataMap = cursor -> getCursorValue(cursor, "type");

        List<String> result = readData(query, dataMap);
        if (result == null) return new ArrayList<>();
        return result;
    }
}
