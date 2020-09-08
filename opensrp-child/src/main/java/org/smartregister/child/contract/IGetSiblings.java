package org.smartregister.child.contract;

import java.util.List;

/**
 * Created by ndegwamartin on 08/09/2020.
 */
public interface IGetSiblings {
    void onSiblingsFetched(List<String> ids);
}
