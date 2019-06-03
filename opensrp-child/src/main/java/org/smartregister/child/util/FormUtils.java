package org.smartregister.child.util;

import android.content.Context;

import org.apache.commons.codec.CharEncoding;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * * Created by ndegwamartin on 2019-05-31.
 * <p>
 * Language agnostic Form Util
 */
public class FormUtils {

    public static final String ecClientRelationships = "ec_client_relationships.json";
    public static final String TAG = FormUtils.class.getCanonicalName();
    public static final String JSON_FORMS_FOLDER = "json.form" + File.pathSeparator;
    public static final String JSON_FORM_EXTENSION = ".json";

    private Context mContext;

    public FormUtils(Context context) {
        mContext = context.getApplicationContext();
    }

    public static FormUtils getInstance(Context ctx) {

        return new FormUtils(ctx);
    }


    private String readFileFromAssetsFolder(String fileName) {
        String fileContents;
        try {
            InputStream is = mContext.getAssets().open(fileName);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            fileContents = new String(buffer, CharEncoding.UTF_8);
        } catch (IOException ex) {
            android.util.Log.e(TAG, ex.toString(), ex);

            return null;
        }

        return fileContents;
    }

    public JSONObject getFormJson(String formIdentity) {
        if (mContext != null) {
            try {
                String locale = mContext.getResources().getConfiguration().locale.getLanguage();
                locale = locale.equalsIgnoreCase("en") ? "" : "-" + locale;


                String filePath = readFileFromAssetsFolder(JSON_FORMS_FOLDER + locale + "/" + formIdentity + JSON_FORM_EXTENSION);

                if (filePath == null) {

                    filePath = readFileFromAssetsFolder(JSON_FORMS_FOLDER + formIdentity + JSON_FORM_EXTENSION);
                }

                return new JSONObject(readFileFromAssetsFolder(filePath));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

}