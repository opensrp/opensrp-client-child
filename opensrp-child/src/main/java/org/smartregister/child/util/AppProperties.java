package org.smartregister.child.util;

import java.util.Properties;

/**
 * Created by ndegwamartin on 2019-06-07.
 */
public class AppProperties extends Properties {

    public Boolean getPropertyBoolean(String key) {

        return Boolean.valueOf(getProperty(key));
    }

    public Integer getPropertyInteger(String key) {

        return Integer.valueOf(getProperty(key));
    }
}
