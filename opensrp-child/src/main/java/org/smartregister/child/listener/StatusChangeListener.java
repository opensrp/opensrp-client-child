package org.smartregister.child.listener;

import java.util.Map;

/**
 * Created by raihan on 4/16/17.
 */
public interface StatusChangeListener {
    void updateStatus();

    void updateStatus(Map<String, String> details);

    void updateClientAttribute(String attributeName, Object attributeValue);
}
