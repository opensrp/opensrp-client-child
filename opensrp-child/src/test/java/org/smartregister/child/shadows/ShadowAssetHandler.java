package org.smartregister.child.shadows;

import android.content.Context;

import org.robolectric.annotation.Implementation;
import org.robolectric.annotation.Implements;
import org.smartregister.child.util.VaccineData;
import org.smartregister.util.AssetHandler;

/**
 * Created by Ephraim Kigamba - ekigamba@ona.io on 2020-02-06
 */

@Implements(AssetHandler.class)
public class ShadowAssetHandler {

    @Implementation
    public static String readFileFromAssetsFolder(String fileName, Context context) {
        if (fileName.equals("special_vaccines.json")) {
            return VaccineData.SPECIAL_VACCINES_JSON;
        } else if (fileName.equals("vaccines.json")) {
            return VaccineData.VACCINES_JSON;
        }

        return "";
    }
}
