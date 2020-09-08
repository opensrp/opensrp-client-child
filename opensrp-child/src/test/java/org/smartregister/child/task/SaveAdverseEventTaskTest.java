package org.smartregister.child.task;

import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.smartregister.Context;
import org.smartregister.child.ChildLibrary;
import org.smartregister.repository.AllSharedPreferences;
import org.smartregister.repository.EventClientRepository;

import java.lang.reflect.Method;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ChildLibrary.class})
public class SaveAdverseEventTaskTest {

    @Mock
    private ChildLibrary childLibrary;

    @Mock
    private EventClientRepository eventClientRepository;

    @Mock
    private Context context;

    @Mock
    private AllSharedPreferences allSharedPreferences;

    private SaveAdverseEventTask saveAdverseEventTask;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        PowerMockito.mockStatic(ChildLibrary.class);
        PowerMockito.when(ChildLibrary.getInstance()).thenReturn(childLibrary);

        String jsonString = "{\"count\":\"1\",\"encounter_type\":\"AEFI\",\"entity_id\":\"\",\"metadata\":{\"start\":{\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_data_type\":\"start\",\"openmrs_entity_id\":\"163137AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"value\":\"2020-09-08 11:30:36\"},\"end\":{\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_data_type\":\"end\",\"openmrs_entity_id\":\"163138AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"value\":\"2020-09-08 11:30:53\"},\"today\":{\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"encounter\",\"openmrs_entity_id\":\"encounter_date\",\"value\":\"08-09-2020\"},\"deviceid\":{\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_data_type\":\"deviceid\",\"openmrs_entity_id\":\"163149AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"value\":\"358240051111110\"},\"subscriberid\":{\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_data_type\":\"subscriberid\",\"openmrs_entity_id\":\"163150AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"value\":\"310260000000000\"},\"simserial\":{\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_data_type\":\"simserial\",\"openmrs_entity_id\":\"163151AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"value\":\"89014103211118510720\"},\"phonenumber\":{\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_data_type\":\"phonenumber\",\"openmrs_entity_id\":\"163152AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"value\":\"+15555215554\"},\"encounter_location\":\"\"},\"step1\":{\"title\":\"Report an adverse event\",\"fields\":[{\"key\":\"Reaction_Vaccine\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"6042AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"openmrs_data_type\":\"select one\",\"type\":\"spinner\",\"hint\":\"Vaccine that caused the reaction \",\"values\":[\"OPV 0 (08-09-2020)\"],\"openmrs_choice_ids\":{\"OPV 0 (08-09-2020)\":\"129578AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"},\"v_required\":{\"value\":true,\"err\":\"Please enter the vaccine that caused the reaction\"},\"value\":\"OPV 0 (08-09-2020)\"},{\"key\":\"Date_Reaction\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"1730AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"type\":\"date_picker\",\"hint\":\"Date that the child started experiencing the reaction \",\"expanded\":false,\"max_date\":\"today\",\"v_required\":{\"value\":\"true\",\"err\":\"Please enter the date child started experiencing reaction\"},\"value\":\"08-09-2020\"},{\"key\":\"Reaction_Description\",\"openmrs_entity_parent\":\"111172AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"160632AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"openmrs_data_type\":\"text\",\"type\":\"edit_text\",\"hint\":\"Describe the reaction \",\"v_required\":{\"value\":\"true\",\"err\":\"Please describe the reaction\"},\"value\":\"sdsds\"},{\"key\":\"Reaction_Referred\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"1648AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"openmrs_data_type\":\"boolean\",\"type\":\"spinner\",\"hint\":\"Child referred? \",\"values\":[\"Yes\",\"No\"],\"openmrs_choice_ids\":{\"Yes\":\"true\",\"No\":\"false\"},\"v_required\":{\"value\":true,\"err\":\"Please specify if child was referred\"},\"value\":\"Yes\"},{\"key\":\"AEFI_Form_Completed\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"163340AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"openmrs_data_type\":\"select one\",\"type\":\"spinner\",\"hint\":\"AEFI form completed?\",\"values\":[\"Yes\",\"No\"],\"openmrs_choice_ids\":{\"Yes\":\"1267AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"No\":\"163339AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"},\"value\":\"Yes\"}]},\"invisible_required_fields\":\"[]\",\"details\":{\"appVersionName\":\"1.14.1-SNAPSHOT\",\"formVersion\":\"\"}}";

        saveAdverseEventTask  = new SaveAdverseEventTask(jsonString, "loc-id", "123", "345", eventClientRepository);
    }

    @Test
    public void testProcessAdverseEvent() throws Exception {
        Method processAdverseEvent = SaveAdverseEventTask.class.getDeclaredMethod("processAdverseEvent");
        processAdverseEvent.setAccessible(true);

        Mockito.doReturn(context).when(childLibrary).context();
        Mockito.doReturn(allSharedPreferences).when(context).allSharedPreferences();
        Mockito.doReturn("loc-123").when(allSharedPreferences).fetchCurrentLocality();

        processAdverseEvent.invoke(saveAdverseEventTask);

        ArgumentCaptor<String> argument = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<JSONObject> argument2 = ArgumentCaptor.forClass(JSONObject.class);

        verify(eventClientRepository).addEvent(argument.capture(), argument2.capture());
        assertEquals("123", argument.getValue());
        assertEquals("loc-123", argument2.getValue().getString("childLocationId"));
    }
}
