package org.smartregister.child;

import androidx.appcompat.app.AppCompatActivity;

import org.smartregister.child.listener.StatusChangeListener;

import java.util.Map;

public class TestAppCompactActivity extends AppCompatActivity implements StatusChangeListener {

    @Override
    public void updateStatus() {
        // No implementation
    }

    @Override
    public void updateStatus(Map<String, String> details) {
        // No implementation
    }

    @Override
    public void updateClientAttribute(String attributeName, Object attributeValue) {
        // No implementation
    }
}
