package org.smartregister.child.listener;

/**
 * Created by raihan on 4/16/17.
 */
public interface StatusChangeListener {
    void updateStatus();

    void updateClientAttribute(String attributeName, Object attributeValue);
}
