package org.smartregister.child;

import androidx.appcompat.app.AppCompatActivity;

import org.smartregister.child.listener.StatusChangeListener;

import java.util.Map;

public class TestAppCompactActivity extends AppCompatActivity implements StatusChangeListener {
    public TestAppCompactActivity() {}

    @Override
    public void updateStatus() {

    }

    @Override
    public void updateStatus(Map<String, String> details) {

    }

    @Override
    public void updateClientAttribute(String attributeName, Object attributeValue) {

    }
}
