package org.smartregister.child.sample.activity;

import org.smartregister.child.activity.BaseChildFormActivity;
import org.smartregister.child.util.Utils;

/**
 * Created by ndegwamartin on 2019-05-31.
 */
public class ChildFormActivity extends BaseChildFormActivity {
    @Override
    protected void attachBaseContext(android.content.Context base) {

        String language = Utils.getLanguage(base);
        super.attachBaseContext(Utils.setAppLocale(base, language));
    }
}
