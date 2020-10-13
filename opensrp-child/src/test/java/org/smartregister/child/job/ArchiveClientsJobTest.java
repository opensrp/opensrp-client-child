package org.smartregister.child.job;

import android.content.Context;
import android.content.Intent;

import com.evernote.android.job.DailyJob;
import com.evernote.android.job.Job;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatcher;
import org.mockito.Mockito;
import org.robolectric.Robolectric;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.shadows.ShadowService;
import org.robolectric.util.ReflectionHelpers;
import org.smartregister.child.BaseUnitTest;
import org.smartregister.child.service.intent.ArchiveClientRecordIntentService;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by Ephraim Kigamba - nek.eam@gmail.com on 13-10-2020.
 */
public class ArchiveClientsJobTest extends BaseUnitTest {

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void onRunDailyJob() {
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