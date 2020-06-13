package org.smartregister.child.contract;

import org.smartregister.commonregistry.CommonPersonObjectClient;

import java.util.Map;

/**
 * Created by ndegwamartin on 2020-04-28.
 */
public interface IChildDetails {
    CommonPersonObjectClient getChildDetails();

    void setChildDetails(Map<String, String> detailsMap);
}
