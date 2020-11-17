package org.smartregister.child.task;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.reflect.Whitebox;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.util.ReflectionHelpers;
import org.smartregister.child.BaseUnitTest;
import org.smartregister.child.ChildLibrary;
import org.smartregister.child.R;
import org.smartregister.child.activity.BaseChildFormActivity;
import org.smartregister.child.domain.ChildMetadata;
import org.smartregister.child.domain.RegisterActionParams;
import org.smartregister.child.provider.RegisterQueryProvider;
import org.smartregister.child.util.Constants;
import org.smartregister.child.util.Utils;
import org.smartregister.child.wrapper.VaccineViewRecordUpdateWrapper;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.commonregistry.CommonRepository;
import org.smartregister.domain.Alert;
import org.smartregister.domain.AlertStatus;
import org.smartregister.immunization.ImmunizationLibrary;
import org.smartregister.immunization.db.VaccineRepo;
import org.smartregister.immunization.domain.GroupVaccineCount;
import org.smartregister.immunization.domain.jsonmapping.Vaccine;
import org.smartregister.immunization.domain.jsonmapping.VaccineGroup;
import org.smartregister.immunization.repository.VaccineRepository;
import org.smartregister.immunization.util.VaccineCache;
import org.smartregister.service.AlertService;
import org.smartregister.util.AppProperties;
import org.smartregister.view.contract.SmartRegisterClient;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by Ephraim Kigamba - ekigamba@ona.io on 2019-09-06
 */

public class VaccinationAsyncTaskTest extends BaseUnitTest {

    @Mock
    private VaccineViewRecordUpdateWrapper vaccineViewRecordUpdateWrapper;

    @Mock
    private View view;

    @Mock
    private TextView textView;

    @Mock
    private ImageView imageView;

    @Mock
    private LinearLayout linearLayout;

    @Mock
    private RegisterActionParams registerActionParams;

    @Mock
    private CommonRepository commonRepository;

    @Mock
    private VaccineRepository vaccineRepository;

    @Mock
    private AlertService alertService;

    private VaccinationAsyncTask vaccinationAsyncTask;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        ImmunizationLibrary immunizationLibrary = Mockito.mock(ImmunizationLibrary.class);

        Map<String, Object> vaccineRepoMap = new HashMap<>();

        List<Vaccine> groupVaccines = new ArrayList<>();
        Vaccine vaccine;

        vaccine = new Vaccine();
        vaccine.setName("opv0");
        groupVaccines.add(vaccine);

        vaccine = new Vaccine();
        vaccine.setName("opv1");
        groupVaccines.add(vaccine);

        vaccine = new Vaccine();
        vaccine.setName("bcg");
        groupVaccines.add(vaccine);

        vaccine = new Vaccine();
        vaccine.setName("0pv2");
        groupVaccines.add(vaccine);

        VaccineGroup vaccineGroup = Mockito.spy(VaccineGroup.class);
        vaccineGroup.vaccines = groupVaccines;

        vaccineRepoMap.put("vaccines.json", Arrays.asList(new VaccineGroup[]{vaccineGroup}));

        PowerMockito.when(immunizationLibrary.getVaccinesConfigJsonMap()).thenReturn(vaccineRepoMap);

        HashMap<String, VaccineCache> vaccineCacheHashMap = new HashMap<>();

        vaccineCacheHashMap.put(Constants.CHILD_TYPE, new VaccineCache());

        ReflectionHelpers.setStaticField(ImmunizationLibrary.class, "vaccineCacheMap", vaccineCacheHashMap);

        HashMap<String, String> vaccineGrouping = new HashMap<>();
        vaccineGrouping.put("bcg", "At Birth");
        vaccineGrouping.put("opv0", "6 weeks");
        vaccineGrouping.put("opv1", "10 weeks");
        vaccineGrouping.put("opv2", "14 weeks");


        ReflectionHelpers.setStaticField(ImmunizationLibrary.class, "instance", immunizationLibrary);

        ChildLibrary childLibrary = Mockito.mock(ChildLibrary.class);
        ReflectionHelpers.setStaticField(ChildLibrary.class, "instance", childLibrary);
        ChildMetadata childMetaData = new ChildMetadata(BaseChildFormActivity.class, null, null, null, true, new RegisterQueryProvider());
        childMetaData.updateChildRegister("test", "test",
                "test", "ChildRegisterEvent",
                "ChildRegisterUpdateEvent", "OOCSEventType",
                "test-config",
                "childRelKey", "out_of_catchment_area_form");
        Mockito.doReturn(childMetaData).when(childLibrary).metadata();
        AppProperties appProperties = Mockito.mock(AppProperties.class);

        Mockito.doReturn(appProperties).when(childLibrary).getProperties();
        Mockito.doReturn(false).when(appProperties).hasProperty(Mockito.anyString());

        when(registerActionParams.getConvertView()).thenReturn(view);
        when(registerActionParams.getProfileInfoView()).thenReturn(view);
        when(registerActionParams.getEntityId()).thenReturn("baseEntityId");

        vaccinationAsyncTask = new VaccinationAsyncTask(registerActionParams
                , commonRepository
                , vaccineRepository
                , alertService
                , RuntimeEnvironment.application);
    }

    @After
    public void tearDown() {
        ReflectionHelpers.setStaticField(ChildLibrary.class, "instance", null);
        ReflectionHelpers.setStaticField(ImmunizationLibrary.class, "instance", null);
    }

    @Test
    public void localizeStateKeyShouldReturn6weeksWhenGivenMixedCase6WeeksStateKey() {

        String localizedStateKey = Utils.localizeStateKey(RuntimeEnvironment.application,"6 WEEKS");

        assertEquals("6 Weeks", localizedStateKey);

    }


    @Test
    public void testGetUpcomingState() throws Exception {
        DateTime dateTime = new DateTime();
        //UPCOMING
        VaccinationAsyncTask.State state = Whitebox.invokeMethod(vaccinationAsyncTask, "getUpcomingState", dateTime);
        assertEquals("UPCOMING", state.name());

        //UPCOMING_NEXT_7_DAYS
        dateTime = dateTime.plusDays(1);
        state = Whitebox.invokeMethod(vaccinationAsyncTask, "getUpcomingState", dateTime);
        assertEquals("UPCOMING_NEXT_7_DAYS", state.name());
    }

    @Test
    public void testGetIsGroupPartial() throws Exception {
        Method getIsGroupPartial = VaccinationAsyncTask.class.getDeclaredMethod("getIsGroupPartial", String.class);
        getIsGroupPartial.setAccessible(true);

        Boolean res = (Boolean) getIsGroupPartial.invoke(vaccinationAsyncTask, "opv0");
        Assert.assertFalse(res);

        Map<String, GroupVaccineCount> groupVaccineCountMap = new HashMap<>();
        groupVaccineCountMap.put("Birth", new GroupVaccineCount(0, 2));
        Whitebox.setInternalState(vaccinationAsyncTask, "groupVaccineCountMap", groupVaccineCountMap);

        res = (Boolean) getIsGroupPartial.invoke(vaccinationAsyncTask, "bcg2");
        Assert.assertTrue(res);

    }

    @Test
    public void testGetAlertMessageWhenVaccineDue() throws Exception {
        Method getAlertMessage = VaccinationAsyncTask.class.getDeclaredMethod("getAlertMessage", VaccinationAsyncTask.State.class, String.class);
        getAlertMessage.setAccessible(true);

        String message = (String) getAlertMessage.invoke(vaccinationAsyncTask, VaccinationAsyncTask.State.DUE, null);
        assertEquals("Due", message);

        message = (String) getAlertMessage.invoke(vaccinationAsyncTask, VaccinationAsyncTask.State.DUE, "opv0");
        assertEquals("opv0\nDue", message);
    }

    @Test
    public void testGetAlertMessageWhenVaccineOverDue() throws Exception {
        Method getAlertMessage = VaccinationAsyncTask.class.getDeclaredMethod("getAlertMessage", VaccinationAsyncTask.State.class, String.class);
        getAlertMessage.setAccessible(true);

        String message = (String) getAlertMessage.invoke(vaccinationAsyncTask, VaccinationAsyncTask.State.OVERDUE, null);
        assertEquals("Overdue", message);

        message = (String) getAlertMessage.invoke(vaccinationAsyncTask, VaccinationAsyncTask.State.OVERDUE, "opv0");
        assertEquals("opv0\nOverdue", message);
    }

    @Test
    public void testGetAlertMessageWhenVaccineIsDone() throws Exception {
        Method getAlertMessage = VaccinationAsyncTask.class.getDeclaredMethod("getAlertMessage", VaccinationAsyncTask.State.class, String.class);
        getAlertMessage.setAccessible(true);

        String message = (String) getAlertMessage.invoke(vaccinationAsyncTask, VaccinationAsyncTask.State.NO_ALERT, null);
        assertEquals("Done Today", message);

        message = (String) getAlertMessage.invoke(vaccinationAsyncTask, VaccinationAsyncTask.State.NO_ALERT, "opv0");
        assertEquals("Done Today", message);
    }

    @Test
    public void testGetAlertMessageWhenVaccineIsComplete() throws Exception {
        Method getAlertMessage = VaccinationAsyncTask.class.getDeclaredMethod("getAlertMessage", VaccinationAsyncTask.State.class, String.class);
        getAlertMessage.setAccessible(true);

        String message = (String) getAlertMessage.invoke(vaccinationAsyncTask, VaccinationAsyncTask.State.FULLY_IMMUNIZED, null);
        assertEquals("Done", message);

        message = (String) getAlertMessage.invoke(vaccinationAsyncTask, VaccinationAsyncTask.State.FULLY_IMMUNIZED, "opv0");
        assertEquals("Done", message);
    }

    @Test
    public void testUpdateRecordVaccinationWhenChildIsFullyImmunized() throws Exception {
        Method updateRecordVaccination = VaccinationAsyncTask.class.getDeclaredMethod("updateRecordVaccination", VaccineViewRecordUpdateWrapper.class);
        updateRecordVaccination.setAccessible(true);

        when(vaccineViewRecordUpdateWrapper.getConvertView()).thenReturn(view);
        when(vaccineViewRecordUpdateWrapper.getLostToFollowUp()).thenReturn("false");
        when(vaccineViewRecordUpdateWrapper.getInactive()).thenReturn("false");
        when(vaccineViewRecordUpdateWrapper.getNv()).thenReturn(null);

        List<org.smartregister.immunization.domain.Vaccine> vaccines = new LinkedList<>();
        org.smartregister.immunization.domain.Vaccine vaccine = new org.smartregister.immunization.domain.Vaccine();
        vaccine.setName("testvaccine");
        vaccines.add(vaccine);
        when(vaccineViewRecordUpdateWrapper.getVaccines()).thenReturn(vaccines);

        when(view.findViewById(R.id.record_vaccination)).thenReturn(view);
        when(view.findViewById(R.id.record_vaccination_text)).thenReturn(textView);
        when(view.findViewById(R.id.record_vaccination_check)).thenReturn(imageView);
        when(view.findViewById(R.id.record_vaccination_harvey_ball)).thenReturn(imageView);
        when(imageView.getParent()).thenReturn(linearLayout);

        updateRecordVaccination.invoke(vaccinationAsyncTask, vaccineViewRecordUpdateWrapper);

        verify(textView).setText(R.string.fully_immunized_label);
    }

    @Test
    public void testUpdateRecordVaccinationWhenChildIsInactive() throws Exception {
        Method updateRecordVaccination = VaccinationAsyncTask.class.getDeclaredMethod("updateRecordVaccination", VaccineViewRecordUpdateWrapper.class);
        updateRecordVaccination.setAccessible(true);

        when(vaccineViewRecordUpdateWrapper.getConvertView()).thenReturn(view);
        when(vaccineViewRecordUpdateWrapper.getLostToFollowUp()).thenReturn("false");
        when(vaccineViewRecordUpdateWrapper.getInactive()).thenReturn("true");

        when(view.findViewById(R.id.record_vaccination)).thenReturn(view);
        when(view.findViewById(R.id.record_vaccination_text)).thenReturn(textView);
        when(view.findViewById(R.id.record_vaccination_check)).thenReturn(imageView);
        when(view.findViewById(R.id.record_vaccination_harvey_ball)).thenReturn(imageView);
        when(imageView.getParent()).thenReturn(linearLayout);

        updateRecordVaccination.invoke(vaccinationAsyncTask, vaccineViewRecordUpdateWrapper);

        verify(textView).setText(R.string.inactive);
    }

    @Test
    public void testUpdateRecordVaccinationWhenChildIsLostToFollowUp() throws Exception {
        Method updateRecordVaccination = VaccinationAsyncTask.class.getDeclaredMethod("updateRecordVaccination", VaccineViewRecordUpdateWrapper.class);
        updateRecordVaccination.setAccessible(true);

        when(vaccineViewRecordUpdateWrapper.getConvertView()).thenReturn(view);
        when(vaccineViewRecordUpdateWrapper.getLostToFollowUp()).thenReturn("true");
        when(vaccineViewRecordUpdateWrapper.getInactive()).thenReturn("false");

        when(view.findViewById(R.id.record_vaccination)).thenReturn(view);
        when(view.findViewById(R.id.record_vaccination_text)).thenReturn(textView);
        when(view.findViewById(R.id.record_vaccination_check)).thenReturn(imageView);
        when(view.findViewById(R.id.record_vaccination_harvey_ball)).thenReturn(imageView);
        when(imageView.getParent()).thenReturn(linearLayout);

        updateRecordVaccination.invoke(vaccinationAsyncTask, vaccineViewRecordUpdateWrapper);

        verify(textView).setText(R.string.lost_to_follow_up_with_nl);
    }

    @Test
    public void testUpdateRecordVaccinationWhenStateIsNoAlert() throws Exception {
        Method updateRecordVaccination = VaccinationAsyncTask.class.getDeclaredMethod("updateRecordVaccination", VaccineViewRecordUpdateWrapper.class);
        updateRecordVaccination.setAccessible(true);

        when(vaccineViewRecordUpdateWrapper.getConvertView()).thenReturn(view);
        when(vaccineViewRecordUpdateWrapper.getLostToFollowUp()).thenReturn("false");
        when(vaccineViewRecordUpdateWrapper.getInactive()).thenReturn("false");

        when(view.findViewById(R.id.record_vaccination)).thenReturn(view);
        when(view.findViewById(R.id.record_vaccination_text)).thenReturn(textView);
        when(view.findViewById(R.id.record_vaccination_check)).thenReturn(imageView);
        when(view.findViewById(R.id.record_vaccination_harvey_ball)).thenReturn(imageView);
        when(imageView.getParent()).thenReturn(linearLayout);

        updateRecordVaccination.invoke(vaccinationAsyncTask, vaccineViewRecordUpdateWrapper);

        verify(textView).setTextColor(RuntimeEnvironment.application.getResources().getColor(R.color.client_list_grey));
    }

    @Test
    public void testUpdateRecordVaccinationWhenStateIsDue() throws Exception {
        Method updateRecordVaccination = VaccinationAsyncTask.class.getDeclaredMethod("updateRecordVaccination", VaccineViewRecordUpdateWrapper.class);
        updateRecordVaccination.setAccessible(true);

        when(vaccineViewRecordUpdateWrapper.getConvertView()).thenReturn(view);
        when(vaccineViewRecordUpdateWrapper.getLostToFollowUp()).thenReturn("false");
        when(vaccineViewRecordUpdateWrapper.getInactive()).thenReturn("false");

        Map<String, Object> nv = new HashMap<>();
        Alert alert = new Alert("caseID", "scheduleName", "visitCode", AlertStatus.normal, "startDate", "expiryDate", true);
        nv.put(Constants.KEY.ALERT, alert);
        when(vaccineViewRecordUpdateWrapper.getNv()).thenReturn(nv);

        when(view.findViewById(R.id.record_vaccination)).thenReturn(view);
        when(view.findViewById(R.id.record_vaccination_text)).thenReturn(textView);
        when(view.findViewById(R.id.record_vaccination_check)).thenReturn(imageView);
        when(view.findViewById(R.id.record_vaccination_harvey_ball)).thenReturn(imageView);
        when(imageView.getParent()).thenReturn(linearLayout);

        updateRecordVaccination.invoke(vaccinationAsyncTask, vaccineViewRecordUpdateWrapper);

        verify(textView).setText("\nDue");
    }

    @Test
    public void testUpdateRecordVaccinationWhenStateIsOverdue() throws Exception {
        Method updateRecordVaccination = VaccinationAsyncTask.class.getDeclaredMethod("updateRecordVaccination", VaccineViewRecordUpdateWrapper.class);
        updateRecordVaccination.setAccessible(true);

        when(vaccineViewRecordUpdateWrapper.getConvertView()).thenReturn(view);
        when(vaccineViewRecordUpdateWrapper.getLostToFollowUp()).thenReturn("false");
        when(vaccineViewRecordUpdateWrapper.getInactive()).thenReturn("false");

        Map<String, Object> nv = new HashMap<>();
        Alert alert = new Alert("caseID", "scheduleName", "visitCode", AlertStatus.urgent, "startDate", "expiryDate", true);
        nv.put(Constants.KEY.ALERT, alert);
        when(vaccineViewRecordUpdateWrapper.getNv()).thenReturn(nv);

        when(view.findViewById(R.id.record_vaccination)).thenReturn(view);
        when(view.findViewById(R.id.record_vaccination_text)).thenReturn(textView);
        when(view.findViewById(R.id.record_vaccination_check)).thenReturn(imageView);
        when(view.findViewById(R.id.record_vaccination_harvey_ball)).thenReturn(imageView);
        when(imageView.getParent()).thenReturn(linearLayout);

        updateRecordVaccination.invoke(vaccinationAsyncTask, vaccineViewRecordUpdateWrapper);

        verify(textView).setText("\nOverdue");
    }

    @Test
    public void testUpdateRecordVaccinationWhenStateIsExpired() throws Exception {
        Method updateRecordVaccination = VaccinationAsyncTask.class.getDeclaredMethod("updateRecordVaccination", VaccineViewRecordUpdateWrapper.class);
        updateRecordVaccination.setAccessible(true);

        when(vaccineViewRecordUpdateWrapper.getConvertView()).thenReturn(view);
        when(vaccineViewRecordUpdateWrapper.getLostToFollowUp()).thenReturn("false");
        when(vaccineViewRecordUpdateWrapper.getInactive()).thenReturn("false");

        Map<String, Object> nv = new HashMap<>();
        Alert alert = new Alert("caseID", "scheduleName", "visitCode", AlertStatus.expired, "startDate", "expiryDate", true);
        nv.put(Constants.KEY.ALERT, alert);
        when(vaccineViewRecordUpdateWrapper.getNv()).thenReturn(nv);

        when(view.findViewById(R.id.record_vaccination)).thenReturn(view);
        when(view.findViewById(R.id.record_vaccination_text)).thenReturn(textView);
        when(view.findViewById(R.id.record_vaccination_check)).thenReturn(imageView);
        when(view.findViewById(R.id.record_vaccination_harvey_ball)).thenReturn(imageView);
        when(imageView.getParent()).thenReturn(linearLayout);

        updateRecordVaccination.invoke(vaccinationAsyncTask, vaccineViewRecordUpdateWrapper);

        verify(textView).setText(R.string.expired_label);
    }

    @Test
    public void testUpdateRecordVaccinationWhenStateIsUpcoming() throws Exception {
        Method updateRecordVaccination = VaccinationAsyncTask.class.getDeclaredMethod("updateRecordVaccination", VaccineViewRecordUpdateWrapper.class);
        updateRecordVaccination.setAccessible(true);

        when(vaccineViewRecordUpdateWrapper.getConvertView()).thenReturn(view);
        when(vaccineViewRecordUpdateWrapper.getLostToFollowUp()).thenReturn("false");
        when(vaccineViewRecordUpdateWrapper.getInactive()).thenReturn("false");

        Map<String, Object> nv = new HashMap<>();
        Alert alert = new Alert("caseID", "scheduleName", "visitCode", AlertStatus.upcoming, "startDate", "expiryDate", true);
        nv.put(Constants.KEY.ALERT, alert);
        when(vaccineViewRecordUpdateWrapper.getNv()).thenReturn(nv);

        when(view.findViewById(R.id.record_vaccination)).thenReturn(view);
        when(view.findViewById(R.id.record_vaccination_text)).thenReturn(textView);
        when(view.findViewById(R.id.record_vaccination_check)).thenReturn(imageView);
        when(view.findViewById(R.id.record_vaccination_harvey_ball)).thenReturn(imageView);
        when(imageView.getParent()).thenReturn(linearLayout);

        updateRecordVaccination.invoke(vaccinationAsyncTask, vaccineViewRecordUpdateWrapper);

        verify(textView).setText("Upcoming\n");
    }

    @Test
    public void testUpdateRecordVaccinationWhenStateIsWaiting() throws Exception {
        Method updateRecordVaccination = VaccinationAsyncTask.class.getDeclaredMethod("updateRecordVaccination", VaccineViewRecordUpdateWrapper.class);
        updateRecordVaccination.setAccessible(true);

        when(vaccineViewRecordUpdateWrapper.getConvertView()).thenReturn(view);
        when(vaccineViewRecordUpdateWrapper.getLostToFollowUp()).thenReturn("false");
        when(vaccineViewRecordUpdateWrapper.getInactive()).thenReturn("false");

        when(vaccineViewRecordUpdateWrapper.getNv()).thenReturn(null);

        when(view.findViewById(R.id.record_vaccination)).thenReturn(view);
        when(view.findViewById(R.id.record_vaccination_text)).thenReturn(textView);
        when(view.findViewById(R.id.record_vaccination_check)).thenReturn(imageView);
        when(view.findViewById(R.id.record_vaccination_harvey_ball)).thenReturn(imageView);
        when(imageView.getParent()).thenReturn(linearLayout);

        updateRecordVaccination.invoke(vaccinationAsyncTask, vaccineViewRecordUpdateWrapper);

        verify(textView).setText(R.string.waiting_label);
    }

    @Test
    public void testUpdateRecordVaccinationWhenStateIsNoAlertAndGroupnameIsNotBlank() throws Exception {
        Method updateRecordVaccination = VaccinationAsyncTask.class.getDeclaredMethod("updateRecordVaccination", VaccineViewRecordUpdateWrapper.class);
        updateRecordVaccination.setAccessible(true);

        when(vaccineViewRecordUpdateWrapper.getConvertView()).thenReturn(view);
        when(vaccineViewRecordUpdateWrapper.getLostToFollowUp()).thenReturn("false");
        when(vaccineViewRecordUpdateWrapper.getInactive()).thenReturn("false");

        Map<String, Object> nv = new HashMap<>();
        nv.put(Constants.KEY.VACCINE, VaccineRepo.Vaccine.opv2);
        when(vaccineViewRecordUpdateWrapper.getNv()).thenReturn(nv);

        List<org.smartregister.immunization.domain.Vaccine> vaccines = new LinkedList<>();
        org.smartregister.immunization.domain.Vaccine vaccine = new org.smartregister.immunization.domain.Vaccine();
        vaccine.setName("opv2");
        vaccines.add(vaccine);
        when(vaccineViewRecordUpdateWrapper.getVaccines()).thenReturn(vaccines);

        ImmunizationLibrary.getInstance().getVaccineCacheMap().get("child").reverseLookupGroupMap.put("opv2", "6 weeks");

        when(view.findViewById(R.id.record_vaccination)).thenReturn(view);
        when(view.findViewById(R.id.record_vaccination_text)).thenReturn(textView);
        when(view.findViewById(R.id.record_vaccination_check)).thenReturn(imageView);
        when(view.findViewById(R.id.record_vaccination_harvey_ball)).thenReturn(imageView);
        when(imageView.getParent()).thenReturn(linearLayout);

        updateRecordVaccination.invoke(vaccinationAsyncTask, vaccineViewRecordUpdateWrapper);

        verify(textView).setTextColor(RuntimeEnvironment.application.getResources().getColor(R.color.client_list_grey));
    }

    @Test
    public void testUpdateRecordVaccinationWhenStateIsUpcomingNext7Days() throws Exception {
        Method updateRecordVaccination = VaccinationAsyncTask.class.getDeclaredMethod("updateRecordVaccination", VaccineViewRecordUpdateWrapper.class);
        updateRecordVaccination.setAccessible(true);

        when(vaccineViewRecordUpdateWrapper.getConvertView()).thenReturn(view);
        when(vaccineViewRecordUpdateWrapper.getLostToFollowUp()).thenReturn("false");
        when(vaccineViewRecordUpdateWrapper.getInactive()).thenReturn("false");

        Map<String, Object> nv = new HashMap<>();
        nv.put(Constants.KEY.DATE, new DateTime(new Date()).plusDays(1));
        when(vaccineViewRecordUpdateWrapper.getNv()).thenReturn(nv);

        Whitebox.setInternalState(vaccinationAsyncTask, "lastVaccineDate", new DateTime(new Date()).minusDays(5).toDate());

        when(view.findViewById(R.id.record_vaccination)).thenReturn(view);
        when(view.findViewById(R.id.record_vaccination_text)).thenReturn(textView);
        when(view.findViewById(R.id.record_vaccination_check)).thenReturn(imageView);
        when(view.findViewById(R.id.record_vaccination_harvey_ball)).thenReturn(imageView);
        when(imageView.getParent()).thenReturn(linearLayout);

        updateRecordVaccination.invoke(vaccinationAsyncTask, vaccineViewRecordUpdateWrapper);

        verify(textView).setText("Upcoming\n");
        verify(textView).setTextColor(RuntimeEnvironment.application.getResources().getColor(R.color.client_list_grey));
    }

    @Test
    public void testUpdateRecordVaccinationWhenAppointmentDateIsNotNull() throws Exception {
        Method updateRecordVaccination = VaccinationAsyncTask.class.getDeclaredMethod("updateRecordVaccination", VaccineViewRecordUpdateWrapper.class);
        updateRecordVaccination.setAccessible(true);

        when(vaccineViewRecordUpdateWrapper.getConvertView()).thenReturn(view);
        when(vaccineViewRecordUpdateWrapper.getLostToFollowUp()).thenReturn("false");
        when(vaccineViewRecordUpdateWrapper.getInactive()).thenReturn("false");
        when(view.findViewById(R.id.child_next_appointment)).thenReturn(textView);

        Map<String, Object> nv = new HashMap<>();
        nv.put(Constants.KEY.DATE, new DateTime(new Date()).plusDays(1));
        when(vaccineViewRecordUpdateWrapper.getNv()).thenReturn(nv);

        Calendar calendar = Calendar.getInstance();
        calendar.set(2020, 9, 8);
        Whitebox.setInternalState(vaccinationAsyncTask, "lastVaccineDate", calendar.getTime());

        when(view.findViewById(R.id.record_vaccination)).thenReturn(view);
        when(view.findViewById(R.id.record_vaccination_text)).thenReturn(textView);
        when(view.findViewById(R.id.record_vaccination_check)).thenReturn(imageView);
        when(view.findViewById(R.id.record_vaccination_harvey_ball)).thenReturn(imageView);
        when(imageView.getParent()).thenReturn(linearLayout);

        updateRecordVaccination.invoke(vaccinationAsyncTask, vaccineViewRecordUpdateWrapper);

        verify(textView).setTextColor(RuntimeEnvironment.application.getResources().getColor(R.color.client_list_grey));

        ArgumentCaptor<Integer> argument = ArgumentCaptor.forClass(Integer.class);
        ArgumentCaptor<Object> argument2 = ArgumentCaptor.forClass(Object.class);
        verify(view).setTag(argument.capture(), argument2.capture());

        assertEquals(R.id.next_appointment_date, (long)argument.getValue());
    }

    @Test
    public void testUpdateViews() throws Exception {
        Method updateViews = VaccinationAsyncTask.class.getDeclaredMethod("updateViews", View.class, SmartRegisterClient.class);
        updateViews.setAccessible(true);

        when(view.findViewById(R.id.record_vaccination)).thenReturn(view);
        when(view.findViewById(R.id.move_to_catchment)).thenReturn(view);
        when(view.findViewById(R.id.child_profile_info_layout)).thenReturn(view);
        when(view.findViewById(R.id.move_to_catchment_text)).thenReturn(textView);

        Map<String, String> map = new HashMap<>();
        map.put(Constants.KEY.ZEIR_ID, "24127");
        CommonPersonObjectClient commonPersonObjectClient = new CommonPersonObjectClient("00ts-ime-hcla-0tib-0eht-ma0i", new HashMap<String, String>(), "Roja");
        commonPersonObjectClient.setColumnmaps(map);

        updateViews.invoke(vaccinationAsyncTask, view, commonPersonObjectClient);

        verify(view).setClickable(true);
        verify(view).setEnabled(true);
    }

    @Test
    public void testExecuteInBackground() throws Exception {
        Method executeInBackground = VaccinationAsyncTask.class.getDeclaredMethod("executeInBackground");
        executeInBackground.setAccessible(true);

        List<org.smartregister.immunization.domain.Vaccine> vaccineList = new ArrayList<>();
        org.smartregister.immunization.domain.Vaccine newVaccine = new org.smartregister.immunization.domain.Vaccine(0l, "baseEntityId", "programClientid", "OPV 0", 0,
                new Date(),
                "anmId", "locationId", "syncStatus", "hia2Status", 0l,
                "eventId", "formSubmissionId", 0, new Date());
        vaccineList.add(newVaccine);
        when(vaccineRepository.findByEntityId("baseEntityId")).thenReturn(vaccineList);

        executeInBackground.invoke(vaccinationAsyncTask);

        verify(vaccineRepository).findByEntityId("baseEntityId");
        verify(alertService).findByEntityId("baseEntityId");
    }
}