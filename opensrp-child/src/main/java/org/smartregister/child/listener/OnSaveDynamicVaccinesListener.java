package org.smartregister.child.listener;

import org.smartregister.child.event.DynamicVaccineType;

/**
 * CallBack listener called when dynamic vaccines have been saved
 */
public interface OnSaveDynamicVaccinesListener {

    void onSaveDynamicVaccine(DynamicVaccineType dynamicVaccineType);

    void onUpdateDynamicVaccine();
}
