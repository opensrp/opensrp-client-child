package org.smartregister.child.job;

import android.content.Context;
import android.content.Intent;

import com.evernote.android.job.DailyJob;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.util.ReflectionHelpers;
import org.smartregister.child.BaseUnitTest;
import org.smartregister.child.service.intent.ArchiveClientRecordIntentService;

import java.lang.ref.WeakReference;


/**
 * Created by Ephraim Kigamba - nek.eam@gmail.com on 13-10-2020.
 */
public class ArchiveClientsJobTest extends BaseUnitTest {

    @Test
    public void onRunDailyJobShouldReturnSuccessAndCallStartService() {
        ArchiveClientsJob archiveClientsJob = new ArchiveClientsJob(ArchiveClientRecordIntentService.class);

        Context context = Mockito.spy(RuntimeEnvironment.application);
        ReflectionHelpers.setField(archiveClientsJob, "mContextReference", new WeakReference<Context>(context));
        ReflectionHelpers.setField(archiveClientsJob, "mApplicationContext", context);

        DailyJob.DailyJobResult dailyJobResult = archiveClientsJob.onRunDailyJob(null);

        // Assertions & verifications
        ArgumentCaptor<Intent> argumentMatcher = ArgumentCaptor.forClass(Intent.class);

        Assert.assertEquals(DailyJob.DailyJobResult.SUCCESS, dailyJobResult);
        Mockito.verify(context).startService(argumentMatcher.capture());
        Assert.assertEquals(ArchiveClientRecordIntentService.class.getName(), argumentMatcher.getValue().getComponent().getClassName());
    }
}