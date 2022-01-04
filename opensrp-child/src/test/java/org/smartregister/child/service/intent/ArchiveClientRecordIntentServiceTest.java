package org.smartregister.child.service.intent;

import android.content.Intent;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.smartregister.child.BasePowerMockUnitTest;
import org.smartregister.child.util.Utils;
import org.smartregister.clientandeventmodel.Event;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by ndegwamartin on 17/11/2020.
 */

@PrepareForTest({Utils.class})
public class ArchiveClientRecordIntentServiceTest extends BasePowerMockUnitTest {
    @Mock
    private Intent intent;

    @Before
    public void setUp() {

        PowerMockito.mockStatic(Utils.class);
    }

    @Test
    public void onHandleIntentShouldInvokeArchiveClients() {
        ArchiveClientRecordIntentService service = Mockito.spy(ArchiveClientRecordIntentService.class);
        service.onHandleIntent(intent);

        Mockito.verify(service, Mockito.times(1)).archiveClients();

    }

    @Test
    public void testArchiveClientsInvokesCreateArchiveRecordEvents() throws Exception {

        List<String> clientIds = Arrays.asList(new String[]{"23232", "1192"});
        ArchiveClientRecordIntentService service = Mockito.mock(ArchiveClientRecordIntentService.class, Mockito.CALLS_REAL_METHODS);
        Mockito.doReturn(clientIds).when(service).getClientIdsToArchive();

        service.archiveClients();

        PowerMockito.verifyStatic(Utils.class);
        ArgumentCaptor<List<String>> listArgumentCaptor = ArgumentCaptor.forClass(List.class);

        Utils.createArchiveRecordEvents(listArgumentCaptor.capture());

        List<String> captured = listArgumentCaptor.getValue();
        Assert.assertNotNull(captured);
        Assert.assertEquals("23232", captured.get(0));
        Assert.assertEquals("1192", captured.get(1));


    }


    @Test
    public void testArchiveClientsInvokesInitiateEventProcessing() throws Exception {

        ArchiveClientRecordIntentService service = Mockito.spy(ArchiveClientRecordIntentService.class);

        List<String> clientIds = Mockito.mock(List.class);
        Mockito.doReturn(clientIds).when(service).getClientIdsToArchive();

        List<Event> archiveRecordEvents = new ArrayList<>();
        Event event = new Event();
        event.setBaseEntityId("base-entity-id-1");
        event.setFormSubmissionId("form-sub-A");
        archiveRecordEvents.add(event);

        event = new Event();
        event.setBaseEntityId("base-entity-id-2");
        event.setFormSubmissionId("form-sub-B");
        archiveRecordEvents.add(event);

        PowerMockito.when(Utils.createArchiveRecordEvents(ArgumentMatchers.<String>anyList())).thenReturn(archiveRecordEvents);

        service.archiveClients();

        PowerMockito.verifyStatic(Utils.class);
        ArgumentCaptor<List<String>> listArgumentCaptor = ArgumentCaptor.forClass(List.class);

        Utils.initiateEventProcessing(listArgumentCaptor.capture());

        List<String> captured = listArgumentCaptor.getValue();
        Assert.assertNotNull(captured);
        Assert.assertEquals("form-sub-A", captured.get(0));
        Assert.assertEquals("form-sub-B", captured.get(1));

        //Verify onArchiveDone callback
        Mockito.verify(service, Mockito.times(1)).onArchiveDone();
    }
}