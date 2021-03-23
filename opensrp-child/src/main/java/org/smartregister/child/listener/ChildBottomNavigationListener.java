package org.smartregister.child.listener;

import android.view.MenuItem;

import androidx.annotation.NonNull;

import org.smartregister.child.R;
import org.smartregister.child.activity.BaseChildRegisterActivity;
import org.smartregister.listener.BottomNavigationListener;

import java.lang.ref.WeakReference;

public class ChildBottomNavigationListener extends BottomNavigationListener {
    private WeakReference<BaseChildRegisterActivity> baseRegisterActivity;

    public ChildBottomNavigationListener(BaseChildRegisterActivity context) {
        super(context);
        this.baseRegisterActivity = new WeakReference<>(context);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == R.id.action_home) {
            getBaseRegisterActivityReference().switchToBaseFragment();
        } else if (item.getItemId() == R.id.action_scan_qr) {
            getBaseRegisterActivityReference().startQrCodeScanner();
        } else if (item.getItemId() == R.id.action_biometric) {
            ((BaseChildRegisterActivity) getBaseRegisterActivityReference()).startBiometricScanner();
        } else if (item.getItemId() == R.id.action_search) {
            getBaseRegisterActivityReference().switchToFragment(1);
        } else if (item.getItemId() == R.id.action_register) {
            getBaseRegisterActivityReference().startRegistration();
        }

        return true;
    }

    public BaseChildRegisterActivity getBaseRegisterActivityReference() {
        return baseRegisterActivity != null ? baseRegisterActivity.get() : null;
    }
}
