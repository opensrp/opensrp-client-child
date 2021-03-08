package org.smartregister.child.listener;

import org.smartregister.child.task.SaveDynamicVaccinesTask;

/**
 * CallBack listener called when dynamic vaccines have been saved
 */
public interface OnSaveDynamicVaccinesListener {
    void onSaveDynamicVaccine(SaveDynamicVaccinesTask.DynamicVaccineTypes dynamicVaccineTypes);
}
