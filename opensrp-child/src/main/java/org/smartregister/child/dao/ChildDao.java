package org.smartregister.child.dao;

import android.database.Cursor;
import android.util.Pair;

import org.apache.commons.lang3.StringUtils;
import org.smartregister.child.util.Constants;
import org.smartregister.dao.AbstractDao;

import java.util.ArrayList;
import java.util.List;

public class ChildDao extends AbstractDao {

    public static List<Pair<String, String>> getChildExtraVaccines(String tableName, final Pair<String, String> columnPair,
                                                                   String baseEntityColumn, String baseEntityId) {
        String query = String.format("SELECT %s,%s FROM %s WHERE %s = '%s'", columnPair.first, columnPair.second, tableName,
                baseEntityColumn, baseEntityId);
        DataMap<Pair<String, String>> dataMap = new DataMap<Pair<String, String>>() {
            @Override
            public Pair<String, String> readCursor(Cursor cursor) {
                return Pair.create(getCursorValue(cursor, columnPair.first), getCursorValue(cursor, columnPair.second));
            }
        };

        List<Pair<String, String>> result = readData(query, dataMap);
        if (result == null) return new ArrayList<>();
        return result;
    }

    /**
     * Retrieve the selected vaccines that are comma separated and create new list
     *
     * @param baseEntityId unique id for the client
     * @return a list of selected vaccines with their dates
     */
    public static List<Pair<String, String>> getChildExtraVaccines(String baseEntityId) {
        List<Pair<String, String>> extraVaccines = getChildExtraVaccines(Constants.Tables.EC_DYNAMIC_VACCINES,
                Pair.create(Constants.KEY.SELECTED_VACCINES, Constants.KEY.VACCINE_DATE), Constants.KEY.ENTITY_ID, baseEntityId);
        List<Pair<String, String>> vaccinesList = new ArrayList<>();
        for (Pair<String, String> vaccine : extraVaccines) {
            if (StringUtils.isNotBlank(vaccine.first) && StringUtils.isNotBlank(vaccine.second)) {
                String[] vaccines = vaccine.first.split(",");
                for (String vac : vaccines) {
                    vaccinesList.add(Pair.create(vac, vaccine.second));
                }
            }
        }
        return vaccinesList;
    }

}
