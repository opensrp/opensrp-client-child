package org.smartregister.child.contract;

import java.util.Map;

/**
 * Created by ndegwamartin on 2020-04-28.
 */
public interface IMotherLookup {
    String lookUpQuery(Map<String, String> entityMap, String tableName);
}
