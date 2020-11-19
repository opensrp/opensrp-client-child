package org.smartregister.child.job;

import android.content.Intent;
import android.support.annotation.NonNull;

import org.smartregister.child.service.intent.ArchiveClientRecordIntentService;
import org.smartregister.job.BaseJob;

public class ArchiveClientsJob extends BaseJob {

    public static final String TAG = "ArchiveClientsJob";

    private final Class<? extends ArchiveClientRecordIntentService> intentServiceClass;

    public ArchiveClientsJob(Class<? extends ArchiveClientRecordIntentService> intentServiceClass) {
        this.intentServiceClass = intentServiceClass;
    }

    @NonNull
    @Override
    protected Result onRunJob(@NonNull Params params) {
        Intent intent = new Intent(getApplicationContext(), intentServiceClass);
        getApplicationContext().startService(intent);
        return Result.SUCCESS;
    }
}
